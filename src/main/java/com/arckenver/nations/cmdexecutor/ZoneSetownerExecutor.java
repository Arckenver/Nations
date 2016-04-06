package com.arckenver.nations.cmdexecutor;

import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Zone;

public class ZoneSetownerExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			if (!ctx.<String>getOne("owner").isPresent())
			{
				src.sendMessage(Text.of(TextColors.RED, "/z setowner <owner>"));
				return CommandResult.success();
			}
			String newOwnerName = ctx.<String>getOne("owner").get();
			Player player = (Player) src;
			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CI));
				return CommandResult.success();
			}
			Zone zone = nation.getZone(player.getLocation());
			if (zone == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.GX));
				return CommandResult.success();
			}
			if (!zone.isOwner(player.getUniqueId()) && !nation.isStaff(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.GV));
				return CommandResult.success();
			}
			UUID newOwner = DataHandler.getPlayerUUID(newOwnerName);
			if (newOwner == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CC));
				return CommandResult.success();
			}
			if (newOwner.equals(zone.getOwner()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.HB));
				return CommandResult.success();
			}
			if (!nation.isCitizen(newOwner))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.HC));
				return CommandResult.success();
			}
			zone.setOwner(newOwner);
			DataHandler.saveNation(nation.getUUID());
			final String zoneName = zone.getName();
			if (newOwner.equals(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.GU.replaceAll("\\{ZONE\\}", zoneName)));
			}
			else
			{
				src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.HD.replaceAll("\\{PLAYER\\}", newOwnerName).replaceAll("\\{ZONE\\}", zoneName)));
				Sponge.getServer().getPlayer(newOwner).ifPresent(
						p -> p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.HE.replaceAll("\\{PLAYER\\}", player.getName()).replaceAll("\\{ZONE\\}", zoneName))));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
