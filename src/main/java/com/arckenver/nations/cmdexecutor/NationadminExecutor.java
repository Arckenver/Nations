package com.arckenver.nations.cmdexecutor;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.LanguageHandler;

public class NationadminExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		src.sendMessage(Text.of(
				TextColors.GOLD, ((src instanceof Player) ? "" : "\n") + "--------{ ",
				TextColors.YELLOW, "/nationadmin",
				TextColors.GOLD, " }--------",
				TextColors.GOLD, "\n/na setname <nation> <name>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BO,
				TextColors.GOLD, "\n/na setpres <nation> <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BN,
				TextColors.GOLD, "\n/na forcejoin <nation> <player>", TextColors.GRAY, " - ", TextColors.YELLOW, 
				TextColors.GOLD, "\n/na forceleave <nation> <player>", TextColors.GRAY, " - ", TextColors.YELLOW, 
				TextColors.GOLD, "\n/na eco <give|take|set> <nation> <amount>", TextColors.GRAY, " - ", TextColors.YELLOW, 
				TextColors.GOLD, "\n/na delete <nation>", TextColors.GRAY, " - ", TextColors.YELLOW, 
				TextColors.GOLD, "\n/na flag <nation> <flag> <true|false>", TextColors.GRAY, " - ", TextColors.YELLOW, 
				TextColors.GOLD, "\n/na perm <nation> <type> <perm> <true|false>", TextColors.GRAY, " - ", TextColors.YELLOW
		));
		return CommandResult.success();
	}
}
