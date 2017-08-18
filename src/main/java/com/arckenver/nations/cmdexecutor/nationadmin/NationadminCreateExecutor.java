package com.arckenver.nations.cmdexecutor.nationadmin;

import java.util.UUID;

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

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.object.Nation;

public class NationadminCreateExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.create")
				.arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
				.executor(new NationadminCreateExecutor())
				.build(), "create");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("name").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na create <name>"));
			return CommandResult.success();
		}
		if (src instanceof Player)
		{
			String nationName = ctx.<String>getOne("name").get();
			if (DataHandler.getNation(nationName) != null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NAMETAKEN));
				return CommandResult.success();
			}
			if (!nationName.matches("[\\p{Alnum}\\p{IsIdeographic}\\p{IsLetter}\"_\"]*"))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NAMEALPHA));
				return CommandResult.success();
			}
			if (nationName.length() < ConfigHandler.getNode("others", "minNationNameLength").getInt() || nationName.length() > ConfigHandler.getNode("others", "maxNationNameLength").getInt())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NAMELENGTH
						.replaceAll("\\{MIN\\}", ConfigHandler.getNode("others", "minNationNameLength").getString())
						.replaceAll("\\{MAX\\}", ConfigHandler.getNode("others", "maxNationNameLength").getString())));
				return CommandResult.success();
			}
			
			Nation nation = new Nation(UUID.randomUUID(), nationName, true);
			DataHandler.addNation(nation);
			src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.SUCCESS_GENERAL));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
