package com.arckenver.nations.cmdexecutor.nationworld;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.LanguageHandler;

public class NationworldExecutor implements CommandExecutor
{
	@Override
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		src.sendMessage(Text.of(
				TextColors.GOLD, ((src instanceof Player) ? "" : "\n") + "--------{ ",
				TextColors.YELLOW, "/nationworld",
				TextColors.GOLD, " }--------",
				TextColors.GOLD, "\n/nw info [world]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BU,
				TextColors.GOLD, "\n/nw list", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BV,
				TextColors.GOLD, "\n/nw enable <world>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BW, 
				TextColors.GOLD, "\n/nw disable <world>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BX,
				TextColors.GOLD, "\n/nw perm <perm> [true|false]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BY,
				TextColors.GOLD, "\n/nw flag <flag> <true|false>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BZ
		));
		return CommandResult.success();
	}
}
