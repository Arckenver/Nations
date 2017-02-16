package com.arckenver.nations.cmdexecutor.nation;

import java.math.BigDecimal;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Point;
import com.arckenver.nations.object.Rect;
import com.arckenver.nations.object.Region;

public class NationClaimExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CI));
				return CommandResult.success();
			}
			if (!nation.isStaff(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CK));
				return CommandResult.success();
			}
			Point a = DataHandler.getFirstPoint(player.getUniqueId());
			Point b = DataHandler.getSecondPoint(player.getUniqueId());
			if (a == null || b == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EA));
				return CommandResult.success();
			}
			if (!ConfigHandler.getNode("worlds").getNode(a.getWorld().getName()).getNode("enabled").getBoolean())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CS));
				return CommandResult.success();
			}
			Rect rect = new Rect(a, b);
			if (!nation.getRegion().isAdjacent(rect))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EB));
				return CommandResult.success();
			}
			if (!DataHandler.canClaim(rect, false, nation.getUUID()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EI));
				return CommandResult.success();
			}
			Region claimed = nation.getRegion().copy();
			claimed.addRect(rect);
			
			if (claimed.size() > nation.maxBlockSize() || claimed.size() < 1)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EG));
				return CommandResult.success();
			}
			
			if (NationsPlugin.getEcoService() == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.DC));
				return CommandResult.success();
			}
			Optional<Account> optAccount = NationsPlugin.getEcoService().getOrCreateAccount("nation-" + nation.getUUID().toString());
			if (!optAccount.isPresent())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.DD));
				return CommandResult.success();
			}
			BigDecimal price = BigDecimal.valueOf((claimed.size() - nation.getRegion().size()) * ConfigHandler.getNode("prices", "blockClaimPrice").getDouble());
			TransactionResult result = optAccount.get().withdraw(NationsPlugin.getEcoService().getDefaultCurrency(), price, NationsPlugin.getCause());
			if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS)
			{
				src.sendMessage(Text.builder()
						.append(Text.of(TextColors.RED, LanguageHandler.DF.split("\\{AMOUNT\\}")[0]))
						.append(Utils.formatPrice(TextColors.RED, price))
						.append(Text.of(TextColors.RED, LanguageHandler.DF.split("\\{AMOUNT\\}")[1])).build());
				return CommandResult.success();
			}
			else if (result.getResult() != ResultType.SUCCESS)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.DN));
				return CommandResult.success();
			}
			else
			{
				if (ConfigHandler.getNode("economy", "serverAccount").getString() != null)
				{
					String serverAccount = ConfigHandler.getNode("economy", "serverAccount").getString();
					Optional<Account> optServerAccount = NationsPlugin.getEcoService().getOrCreateAccount(serverAccount);
					TransactionResult resultServer = optServerAccount.get().deposit(NationsPlugin.getEcoService().getDefaultCurrency(), price, NationsPlugin.getCause());

					if (resultServer.getResult() != ResultType.SUCCESS)
					{
						NationsPlugin.getLogger().error("Error Giving money to SERVER account");
					}
				}
			}
			
			nation.setRegion(claimed);
			DataHandler.addToWorldChunks(nation);
			DataHandler.saveNation(nation.getUUID());
			src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.EH));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
