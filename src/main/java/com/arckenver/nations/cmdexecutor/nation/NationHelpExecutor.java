package com.arckenver.nations.cmdexecutor.nation;

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

public class NationHelpExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.help")
				.arguments()
				.executor(new NationHelpExecutor())
				.build(), "help", "?");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		List<Text> contents = new ArrayList<>();

		contents.add(Text.of(TextColors.GOLD, "/n info [nation]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_INFO));
		contents.add(Text.of(TextColors.GOLD, "/n here", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_HERE));
		contents.add(Text.of(TextColors.GOLD, "/n cost", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_COST));
		contents.add(Text.of(TextColors.GOLD, "/n see", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_SEE));
		contents.add(Text.of(TextColors.GOLD, "/n list", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_LIST));
		contents.add(Text.of(TextColors.GOLD, "/n create <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_CREATE));
		contents.add(Text.of(TextColors.GOLD, "/n deposit <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_DEPOSIT));
		contents.add(Text.of(TextColors.GOLD, "/n withdraw <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_WITHDRAW));
		contents.add(Text.of(TextColors.GOLD, "/n claim", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_CLAIM));
		contents.add(Text.of(TextColors.GOLD, "/n unclaim", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_UNCLAIM));
		contents.add(Text.of(TextColors.GOLD, "/n invite <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_INVITE));
		contents.add(Text.of(TextColors.GOLD, "/n join <nation>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_JOIN));
		contents.add(Text.of(TextColors.GOLD, "/n kick <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_KICK));
		contents.add(Text.of(TextColors.GOLD, "/n leave", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_LEAVE));
		contents.add(Text.of(TextColors.GOLD, "/n resign <successor>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_RESIGN));
		contents.add(Text.of(TextColors.GOLD, "/n minister <add/remove> <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_MINISTER));
		contents.add(Text.of(TextColors.GOLD, "/n citizen <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_CITIZEN));
		contents.add(Text.of(TextColors.GOLD, "/n chat", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_CHAT));
		contents.add(Text.of(TextColors.GOLD, "/n perm <type> <perm> [true|false]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_PERM));
		contents.add(Text.of(TextColors.GOLD, "/n flag <flag> <true|false>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_FLAG));
		contents.add(Text.of(TextColors.GOLD, "/n taxes <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_TAXES));
		contents.add(Text.of(TextColors.GOLD, "/n spawn [name]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_SPAWN));
		contents.add(Text.of(TextColors.GOLD, "/n home", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_HOME));
		contents.add(Text.of(TextColors.GOLD, "/n setname <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_SETNAME));
		contents.add(Text.of(TextColors.GOLD, "/n settag [tag]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_SETTAG));
		contents.add(Text.of(TextColors.GOLD, "/n visit <nation> [name]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_VISIT));
		contents.add(Text.of(TextColors.GOLD, "/n setspawn <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_SETSPAWN));
		contents.add(Text.of(TextColors.GOLD, "/n delspawn <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_DELSPAWN));
		contents.add(Text.of(TextColors.GOLD, "/n buyextra <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_N_BUYEXTRA));

		PaginationList.builder()
		.title(Text.of(TextColors.GOLD, "{ ", TextColors.YELLOW, "/nation", TextColors.GOLD, " }"))
		.contents(contents)
		.padding(Text.of("-"))
		.sendTo(src);
		return CommandResult.success();
	}
}
