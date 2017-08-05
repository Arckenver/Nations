package com.arckenver.nations.cmdexecutor.nationadmin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.LanguageHandler;

public class NationadminExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.help")
				.arguments()
				.executor(new NationadminExecutor())
				.build(), "help", "?");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		List<Text> contents = new ArrayList<>();

		contents.add(Text.of(TextColors.GOLD, "/na reload", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_RELOAD));
		contents.add(Text.of(TextColors.GOLD, "/na forceupkeep", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_FORCEKEEPUP));
		contents.add(Text.of(TextColors.GOLD, "/na create <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_CREATE));
		contents.add(Text.of(TextColors.GOLD, "/na claim <nation>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_CLAIM));
		contents.add(Text.of(TextColors.GOLD, "/na unclaim <nation>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_UNCLAIM));
		contents.add(Text.of(TextColors.GOLD, "/na delete <nation>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_DELETE));
		contents.add(Text.of(TextColors.GOLD, "/na setname <nation> <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_SETNAME));
		contents.add(Text.of(TextColors.GOLD, "/na settag <nation> <tag>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_SETTAG));
		contents.add(Text.of(TextColors.GOLD, "/na setpres <nation> <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_SETPRES));
		contents.add(Text.of(TextColors.GOLD, "/na setspawn <nation> <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_SETSPAWN));
		contents.add(Text.of(TextColors.GOLD, "/na delspawn <nation> <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_DELSPAWN));
		contents.add(Text.of(TextColors.GOLD, "/na forcejoin <nation> <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_FORCEJOIN));
		contents.add(Text.of(TextColors.GOLD, "/na forceleave <nation> <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_FORCELEAVE));
		contents.add(Text.of(TextColors.GOLD, "/na eco <give|take|set> <nation> <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_ECO));
		contents.add(Text.of(TextColors.GOLD, "/na perm <nation> <type> <perm> [true|false]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_PERM));
		contents.add(Text.of(TextColors.GOLD, "/na flag <nation> <flag> [true|false]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_FLAG));
		contents.add(Text.of(TextColors.GOLD, "/na spy", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_SPY));
		contents.add(Text.of(TextColors.GOLD, "/na extra <give|take|set> <nation> <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_EXTRA));
		contents.add(Text.of(TextColors.GOLD, "/na extraplayer <give|take|set> <player> <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_EXTRAPLAYER));
		contents.add(Text.of(TextColors.GOLD, "/na extraspawn <give|take|set> <nation> <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_EXTRASPAWN));
		contents.add(Text.of(TextColors.GOLD, "/na extraspawnplayer <give|take|set> <player> <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_NA_EXTRASPAWNPLAYER));

		PaginationList.builder()
		.title(Text.of(TextColors.GOLD, "{ ", TextColors.YELLOW, "/nationadmin", TextColors.GOLD, " }"))
		.contents(contents)
		.padding(Text.of("-"))
		.sendTo(src);
		return CommandResult.success();
	}
}
