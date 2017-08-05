package com.arckenver.nations.cmdexecutor.nation;

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
import com.arckenver.nations.cmdelement.CitizenNameElement;
import com.arckenver.nations.object.Nation;
import com.google.common.collect.ImmutableMap;

public class NationMinisterExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.minister")
				.arguments(
						GenericArguments.optional(GenericArguments.choices(Text.of("add|remove"),
								ImmutableMap.<String, String> builder()
										.put("add", "add")
										.put("remove", "remove")
										.build())),
						GenericArguments.optional(new CitizenNameElement(Text.of("citizen"))))
				.executor(new NationMinisterExecutor())
				.build(), "minister");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NONATION));
				return CommandResult.success();
			}
			if (!nation.isPresident(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_NATIONPRES));
				return CommandResult.success();
			}
			if (!ctx.<String>getOne("add|remove").isPresent() || !ctx.<String>getOne("citizen").isPresent())
			{
				src.sendMessage(Text.of(TextColors.YELLOW, "/n minister add <citizen>\n/n minister remove <citizen>"));
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
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_HANDLEMINISTER));
				return CommandResult.success();
			}
			if (addOrRemove.equalsIgnoreCase("add"))
			{
				if (nation.isMinister(uuid))
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_ALREADYMINISTER.replaceAll("\\{PLAYER\\}", playerName)));
					return CommandResult.success();
				}
				nation.addMinister(uuid);
				DataHandler.saveNation(nation.getUUID());
				src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.SUCCESS_ADDMINISTER.replaceAll("\\{PLAYER\\}", playerName)));
				Sponge.getServer().getPlayer(uuid).ifPresent(
						p -> p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.INFO_ADDMINISTER.replaceAll("\\{PLAYER\\}", player.getName()))));
			}
			else if (addOrRemove.equalsIgnoreCase("remove"))
			{
				if (!nation.isMinister(uuid))
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOMINISTER.replaceAll("\\{PLAYER\\}", playerName)));
					return CommandResult.success();
				}
				nation.removeMinister(uuid);
				DataHandler.saveNation(nation.getUUID());
				src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.SUCCESS_DELMINISTER.replaceAll("\\{PLAYER\\}", playerName)));
				Sponge.getServer().getPlayer(uuid).ifPresent(
						p -> p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.INFO_DELMINISTER.replaceAll("\\{PLAYER\\}", player.getName()))));
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
