package com.arckenver.nations.cmdexecutor.zone;

import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.cmdelement.PlayerNameElement;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Zone;

public class ZoneSetownerExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.setowner")
				.arguments(GenericArguments.optional(new PlayerNameElement(Text.of("owner"))))
				.executor(new ZoneSetownerExecutor())
				.build(), "setowner");
	}

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
			Nation nation = DataHandler.getNation(player.getLocation());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NEEDSTANDNATION));
				return CommandResult.success();
			}
			Zone zone = nation.getZone(player.getLocation());
			if (zone == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NEEDSTANDZONESELF));
				return CommandResult.success();
			}
			if (!zone.isOwner(player.getUniqueId()) && !nation.isStaff(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOOWNER));
				return CommandResult.success();
			}
			UUID newOwner = DataHandler.getPlayerUUID(newOwnerName);
			if (newOwner == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADPLAYERNAME));
				return CommandResult.success();
			}
			if (newOwner.equals(zone.getOwner()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_ALREADYOWNER));
				return CommandResult.success();
			}
			if (!nation.isCitizen(newOwner))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_OWNERNEEDNATION));
				return CommandResult.success();
			}
			zone.setOwner(newOwner);
			DataHandler.saveNation(nation.getUUID());
			final String zoneName = zone.getName();
			if (newOwner.equals(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.SUCCESS_SETOWNER.replaceAll("\\{ZONE\\}", zoneName)));
			}
			else
			{
				src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.SUCCESS_CHANGEOWNER.replaceAll("\\{PLAYER\\}", newOwnerName).replaceAll("\\{ZONE\\}", zoneName)));
				Sponge.getServer().getPlayer(newOwner).ifPresent(
						p -> p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.INFO_CHANGEOWNER.replaceAll("\\{PLAYER\\}", player.getName()).replaceAll("\\{ZONE\\}", zoneName))));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
