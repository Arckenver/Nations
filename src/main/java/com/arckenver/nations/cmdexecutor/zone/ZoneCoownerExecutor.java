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
import com.google.common.collect.ImmutableMap;

public class ZoneCoownerExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.coowner")
				.arguments(
						GenericArguments.optional(GenericArguments.choices(Text.of("add|remove"),
								ImmutableMap.<String, String> builder()
										.put("add", "add")
										.put("remove", "remove")
										.build())),
						GenericArguments.optional(new PlayerNameElement(Text.of("citizen"))))
				.executor(new ZoneCoownerExecutor())
				.build(), "coowner");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
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
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOSTANDZONENATION));
				return CommandResult.success();
			}
			final String zoneName = zone.getName();
			if (!zone.isOwner(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_NOTOWNER));
				return CommandResult.success();
			}
			if (!ctx.<String>getOne("add|remove").isPresent() || !ctx.<String>getOne("citizen").isPresent())
			{
				src.sendMessage(Text.of(TextColors.YELLOW, "/z coowner add <citizen>\n/z coowner remove <citizen>"));
				return CommandResult.success();
			}
			String addOrRemove = ctx.<String>getOne("add|remove").get();
			String playerName = ctx.<String>getOne("citizen").get();
			UUID uuid = DataHandler.getPlayerUUID(playerName);
			if (uuid == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADPLAYERNAME));
				return CommandResult.success();
			}
			if (player.getUniqueId().equals(uuid))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_MANAGECOOWNER));
				return CommandResult.success();
			}
			if (addOrRemove.equalsIgnoreCase("add"))
			{
				if (zone.isCoowner(uuid))
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_ALREADYCOOWNER.replaceAll("\\{PLAYER\\}", playerName)));
					return CommandResult.success();
				}
				zone.addCoowner(uuid);
				DataHandler.saveNation(nation.getUUID());
				src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.SUCCESS_ADDCOOWNER.replaceAll("\\{PLAYER\\}", playerName)));
				Sponge.getServer().getPlayer(uuid).ifPresent(
						p -> p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.INFO_ADDCOOWNER.replaceAll("\\{PLAYER\\}", player.getName()).replaceAll("\\{ZONE\\}", zoneName))));
			}
			else if (addOrRemove.equalsIgnoreCase("remove"))
			{
				if (!nation.isMinister(uuid))
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.INFO_ALREADYNOCOOWNER.replaceAll("\\{PLAYER\\}", playerName)));
					return CommandResult.success();
				}
				zone.removeCoowner(uuid);
				DataHandler.saveNation(nation.getUUID());
				src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.SUCCESS_DELCOOWNER.replaceAll("\\{PLAYER\\}", playerName)));
				Sponge.getServer().getPlayer(uuid).ifPresent(
						p -> p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.INFO_DELCOOWNER.replaceAll("\\{PLAYER\\}", player.getName()).replaceAll("\\{ZONE\\}", zoneName))));
			}
			else
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADARG_AR));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
