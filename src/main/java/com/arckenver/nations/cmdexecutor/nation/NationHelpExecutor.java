package com.arckenver.nations.cmdexecutor.nation;

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

public class NationHelpExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		List<Text> contents = new ArrayList<>();

		contents.add(Text.of(TextColors.GOLD, "/n info [nation]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AA));
		contents.add(Text.of(TextColors.GOLD, "/n here", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AB));
		contents.add(Text.of(TextColors.GOLD, "/n cost", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.DY));
		contents.add(Text.of(TextColors.GOLD, "/n list", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AC));
		contents.add(Text.of(TextColors.GOLD, "/n create <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AD));
		contents.add(Text.of(TextColors.GOLD, "/n deposit <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AE));
		contents.add(Text.of(TextColors.GOLD, "/n withdraw <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AF));
		contents.add(Text.of(TextColors.GOLD, "/n claim", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AG));
		contents.add(Text.of(TextColors.GOLD, "/n unclaim", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AH));
		contents.add(Text.of(TextColors.GOLD, "/n invite <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AI));
		contents.add(Text.of(TextColors.GOLD, "/n join <nation>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AJ));
		contents.add(Text.of(TextColors.GOLD, "/n kick <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AK));
		contents.add(Text.of(TextColors.GOLD, "/n leave", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AL));
		contents.add(Text.of(TextColors.GOLD, "/n resign <successor>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AM));
		contents.add(Text.of(TextColors.GOLD, "/n minister <add/remove> <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AN));
		contents.add(Text.of(TextColors.GOLD, "/n citizen <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AU));
		contents.add(Text.of(TextColors.GOLD, "/n chat", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AW));
		contents.add(Text.of(TextColors.GOLD, "/n perm <type> <perm> [true|false]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AO));
		contents.add(Text.of(TextColors.GOLD, "/n flag <flag> <true|false>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AP));
		contents.add(Text.of(TextColors.GOLD, "/n taxes <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AV));
		contents.add(Text.of(TextColors.GOLD, "/n spawn [name]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AQ));
		contents.add(Text.of(TextColors.GOLD, "/n home", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HW));
		contents.add(Text.of(TextColors.GOLD, "/n setname <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BN));
		contents.add(Text.of(TextColors.GOLD, "/n visit <nation> [name]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AX));
		contents.add(Text.of(TextColors.GOLD, "/n setspawn <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AR));
		contents.add(Text.of(TextColors.GOLD, "/n delspawn <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AS));
		contents.add(Text.of(TextColors.GOLD, "/n buyextra <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AT));

		PaginationList.builder()
		.title(Text.of(TextColors.GOLD, "{ ", TextColors.YELLOW, "/nation", TextColors.GOLD, " }"))
		.contents(contents)
		.padding(Text.of("-"))
		.sendTo(src);
		return CommandResult.success();
	}
}
