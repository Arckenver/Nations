package com.arckenver.nations.cmdexecutor.nation;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.object.Nation;

public class NationSetnameExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.setname")
				.arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
				.executor(new NationSetnameExecutor())
				.build(), "setname", "rename");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			if (!ctx.<String>getOne("name").isPresent())
			{
				src.sendMessage(Text.of(TextColors.YELLOW, "/n setname <name>"));
				return CommandResult.success();
			}
			Player player = (Player) src;
			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NONATION));
				return CommandResult.success();
			}
			if (!nation.isStaff(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_NATIONSTAFF));
				return CommandResult.success();
			}
			String newName = ctx.<String>getOne("name").get();
			if (DataHandler.getNation(newName) != null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NAMETAKEN));
				return CommandResult.success();
			}
			if (DataHandler.getNationByTag(newName) != null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_TAGTAKEN));
				return CommandResult.success();
			}
			if (!newName.matches("[\\p{Alnum}\\p{IsIdeographic}\\p{IsLetter}\"_\"]*"))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NAMEALPHA));
				return CommandResult.success();
			}
			if (newName.length() < ConfigHandler.getNode("others", "minNationNameLength").getInt() || newName.length() > ConfigHandler.getNode("others", "maxNationNameLength").getInt())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NAMELENGTH
						.replaceAll("\\{MIN\\}", ConfigHandler.getNode("others", "minNationNameLength").getString())
						.replaceAll("\\{MAX\\}", ConfigHandler.getNode("others", "maxNationNameLength").getString())));
				return CommandResult.success();
			}
			String oldName = nation.getName();
			nation.setName(newName);
			DataHandler.saveNation(nation.getUUID());
			MessageChannel.TO_ALL.send(Text.of(TextColors.AQUA, LanguageHandler.INFO_RENAME
					.replaceAll("\\{OLDNAME\\}", oldName)
					.replaceAll("\\{NEWNAME\\}", nation.getName())));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
