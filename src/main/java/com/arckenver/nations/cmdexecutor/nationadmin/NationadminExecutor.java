package com.arckenver.nations.cmdexecutor.nationadmin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.LanguageHandler;

public class NationadminExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		List<Text> contents = new ArrayList<>();

		contents.add(Text.of(TextColors.GOLD, "/na reload", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AZ));
		contents.add(Text.of(TextColors.GOLD, "/na forceupkeep", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.DZ));
		contents.add(Text.of(TextColors.GOLD, "/na create <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BL));
		contents.add(Text.of(TextColors.GOLD, "/na claim <nation>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AY));
		contents.add(Text.of(TextColors.GOLD, "/na unclaim <nation>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.LI));
		contents.add(Text.of(TextColors.GOLD, "/na delete <nation>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BM));
		contents.add(Text.of(TextColors.GOLD, "/na setname <nation> <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BN));
		contents.add(Text.of(TextColors.GOLD, "/na setpres <nation> <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BO));
		contents.add(Text.of(TextColors.GOLD, "/na forcejoin <nation> <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BP));
		contents.add(Text.of(TextColors.GOLD, "/na forceleave <nation> <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BQ));
		contents.add(Text.of(TextColors.GOLD, "/na eco <give|take|set> <nation> <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BR));
		contents.add(Text.of(TextColors.GOLD, "/na perm <nation> <type> <perm> <true|false>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BS));
		contents.add(Text.of(TextColors.GOLD, "/na flag <nation> <flag> <true|false>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BT));
		contents.add(Text.of(TextColors.GOLD, "/na spy", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.DX));
		contents.add(Text.of(TextColors.GOLD, "/na extra <give|take|set> <nation> <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HY));
		contents.add(Text.of(TextColors.GOLD, "/na extraplayer <give|take|set> <player> <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HZ));
		contents.add(Text.of(TextColors.GOLD, "/na extraspawn <give|take|set> <nation> <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.LG));
		contents.add(Text.of(TextColors.GOLD, "/na extraspawnplayer <give|take|set> <player> <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.LH));

		PaginationList.builder()
		.title(Text.of(TextColors.GOLD, "{ ", TextColors.YELLOW, "/nationadmin", TextColors.GOLD, " }"))
		.contents(contents)
		.padding(Text.of("-"))
		.sendTo(src);
		return CommandResult.success();
	}
}
