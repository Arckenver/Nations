package com.arckenver.nations.cmdexecutor.nation;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Rect;

public class NationCreateExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("name").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/n create <name>"));
			return CommandResult.success();
		}
		if (src instanceof Player)
		{
			Player player = (Player) src;
			if (!ConfigHandler.getNode("worlds").getNode(player.getWorld().getName()).getNode("enabled").getBoolean())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CS));
				return CommandResult.success();
			}
			if (DataHandler.getNationOfPlayer(player.getUniqueId()) != null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EK));
				return CommandResult.success();
			}
			String nationName = ctx.<String>getOne("name").get();
			if (DataHandler.getNation(nationName) != null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EL));
				return CommandResult.success();
			}
			if (!nationName.matches("[a-zA-Z\\._-]{1,}"))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EM));
				return CommandResult.success();
			}
			if (nationName.length() < ConfigHandler.getNode("others", "minNationNameLength").getInt() || nationName.length() > ConfigHandler.getNode("others", "maxNationNameLength").getInt())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EN
						.replaceAll("\\{MIN\\}", ConfigHandler.getNode("others", "minNationNameLength").getString())
						.replaceAll("\\{MAX\\}", ConfigHandler.getNode("others", "maxNationNameLength").getString())));
				return CommandResult.success();
			}
			
			Location<World> loc = player.getLocation();
			if (!DataHandler.canClaim(loc, false))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EI));
				return CommandResult.success();
			}
			
			if (NationsPlugin.getEcoService() == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.DC));
				return CommandResult.success();
			}
			Optional<UniqueAccount> optAccount = NationsPlugin.getEcoService().getOrCreateAccount(player.getUniqueId());
			if (!optAccount.isPresent())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.DO));
				return CommandResult.success();
			}
			BigDecimal price = BigDecimal.valueOf(ConfigHandler.getNode("prices", "nationCreationPrice").getDouble());
			TransactionResult result = optAccount.get().withdraw(NationsPlugin.getEcoService().getDefaultCurrency(), price, NationsPlugin.getCause());
			if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS)
			{
				src.sendMessage(Text.builder()
						.append(Text.of(TextColors.RED, LanguageHandler.DE.split("\\{AMOUNT\\}")[0]))
						.append(Utils.formatPrice(TextColors.RED, price))
						.append(Text.of(TextColors.RED, LanguageHandler.DE.split("\\{AMOUNT\\}")[1])).build());
				return CommandResult.success();
			}
			else if (result.getResult() != ResultType.SUCCESS)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.DN));
				return CommandResult.success();
			}
			
			Nation nation = new Nation(UUID.randomUUID(), nationName);
			nation.addSpawn("home", loc);
			nation.addCitizen(player.getUniqueId());
			nation.setPresident(player.getUniqueId());
			nation.getRegion().addRect(new Rect(player.getWorld().getUniqueId(), loc.getBlockX(), loc.getBlockX(), loc.getBlockZ(), loc.getBlockZ()));
			Optional<Account> optNationAccount = NationsPlugin.getEcoService().getOrCreateAccount("nation-" + nation.getUUID().toString());
			if (!optNationAccount.isPresent())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EO));
				NationsPlugin.getLogger().error("Could not create nation's account on the economy service !");
				return CommandResult.success();
			}
			optNationAccount.get().setBalance(NationsPlugin.getEcoService().getDefaultCurrency(), BigDecimal.ZERO, NationsPlugin.getCause());
			DataHandler.addNation(nation);
			DataHandler.addToWorldChunks(nation);
			MessageChannel.TO_ALL.send(Text.of(TextColors.AQUA, LanguageHandler.EP.replaceAll("\\{PLAYER\\}", player.getName()).replaceAll("\\{NATION\\}", nationName)));
			src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.EQ.replaceAll("\\{NATION\\}", nationName)));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
