package com.arckenver.nations.cmdexecutor.nation;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.LanguageHandler;

public class NationExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		src.sendMessage(Text.of(
				TextColors.GOLD, ((src instanceof Player) ? "" : "\n") + "--------{ ",
				TextColors.YELLOW, "/nation",
				TextColors.GOLD, " }--------",
				TextColors.GOLD, "\n/n info [nation]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AA,
				TextColors.GOLD, "\n/n here", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AB,
				TextColors.GOLD, "\n/n list", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AC,
				TextColors.GOLD, "\n/n create <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AD,
				TextColors.GOLD, "\n/n deposit <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AE,
				TextColors.GOLD, "\n/n withdraw <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AF,
				TextColors.GOLD, "\n/n claim", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AG,
				TextColors.GOLD, "\n/n unclaim", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AH,
				TextColors.GOLD, "\n/n invite <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AI,
				TextColors.GOLD, "\n/n join <nation>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AJ,
				TextColors.GOLD, "\n/n kick <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AK,
				TextColors.GOLD, "\n/n leave", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AL,
				TextColors.GOLD, "\n/n resign <successor>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AM,
				TextColors.GOLD, "\n/n minister <add/remove> <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AN,
				TextColors.GOLD, "\n/n citizen <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AU,
				TextColors.GOLD, "\n/n chat", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AW,
				TextColors.GOLD, "\n/n perm <type> <perm> [true|false]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AO,
				TextColors.GOLD, "\n/n flag <flag> <true|false>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AP,
				TextColors.GOLD, "\n/n taxes <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AV,
				TextColors.GOLD, "\n/n spawn <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AQ,
				TextColors.GOLD, "\n/n setspawn <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AR,
				TextColors.GOLD, "\n/n delspawn <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AS,
				TextColors.GOLD, "\n/n buyextra <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.AT));
		return CommandResult.success();
	}
}
