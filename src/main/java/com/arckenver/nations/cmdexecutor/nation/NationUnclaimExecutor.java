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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Point;
import com.arckenver.nations.object.Rect;
import com.arckenver.nations.object.Region;
import com.arckenver.nations.object.Zone;
import com.flowpowered.math.vector.Vector2i;

public class NationUnclaimExecutor implements CommandExecutor
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
			if (!nation.getRegion().intersects(rect))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EC));
				return CommandResult.success();
			}
			for (Location<World> spawn : nation.getSpawns().values())
			{
				if (rect.isInside(new Vector2i(spawn.getBlockX(), spawn.getBlockZ())))
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EF));
					return CommandResult.success();
				}
			}
			for (Zone zone : nation.getZones().values())
			{
				if (zone.getRect().intersects(rect))
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.HF));
					return CommandResult.success();
				}
			}
			Region claimed = nation.getRegion().copy();
			claimed.removeRect(rect);
			int toUnclaim = nation.getRegion().size() - claimed.size();
			
			BigDecimal refund = BigDecimal.valueOf(0);
			if (ConfigHandler.getNode("prices", "unclaimRefundPercentage").getInt() != 0)
			{
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
				refund = BigDecimal.valueOf(toUnclaim * ConfigHandler.getNode("prices", "blockClaimPrice").getInt() * (ConfigHandler.getNode("prices", "unclaimRefundPercentage").getInt() / 100));
				TransactionResult result = optAccount.get().deposit(NationsPlugin.getEcoService().getDefaultCurrency(), refund, NationsPlugin.getCause());
				if (result.getResult() != ResultType.SUCCESS)
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.DN));
					return CommandResult.success();
				}
			}
			
			
			nation.setRegion(claimed);
			DataHandler.addToWorldChunks(nation);
			DataHandler.saveNation(nation.getUUID());
			if (!refund.equals(BigDecimal.ZERO))
			{
				String str = LanguageHandler.DJ.replaceAll("\\{NUM\\}", Integer.toString(toUnclaim)).replaceAll("\\{PRECENT\\}", ConfigHandler.getNode("prices", "blockClaimPrice").getString());
				src.sendMessage(Text.builder()
						.append(Text.of(TextColors.AQUA, str.split("\\{AMOUNT\\}")[0]))
						.append(Utils.formatPrice(TextColors.AQUA, refund))
						.append(Text.of(TextColors.AQUA, str.split("\\{AMOUNT\\}")[1])).build());
			}
			else
			{
				src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.DK));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
