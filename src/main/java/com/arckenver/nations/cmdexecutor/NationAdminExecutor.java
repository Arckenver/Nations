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

public class NationAdminExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		src.sendMessage(Text.of(
				TextColors.GOLD, ((src instanceof Player) ? "" : "\n") + "--------{ ",
				TextColors.YELLOW, "/nationadmin",
				TextColors.GOLD, " }--------",
				TextColors.GOLD, "\n/na setpres", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BN,
				TextColors.GOLD, "\n/na setname", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.BO
		));
		return CommandResult.success();
	}
}
