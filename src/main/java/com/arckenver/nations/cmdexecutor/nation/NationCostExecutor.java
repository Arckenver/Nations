package com.arckenver.nations.cmdexecutor.nation;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.LanguageHandler;

public class NationCostExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		src.sendMessage(Text.of(
				TextColors.GOLD, ((src instanceof Player) ? "" : "\n") + "--------{ ",
				TextColors.YELLOW, LanguageHandler.LA,
				TextColors.GOLD, " }--------",
				TextColors.GOLD, "\n", LanguageHandler.LB, TextColors.GRAY, " - ", TextColors.YELLOW, ConfigHandler.getNode("prices", "nationCreationPrice").getDouble(),
				TextColors.GOLD, "\n", LanguageHandler.LC, TextColors.GRAY, " - ", TextColors.YELLOW, ConfigHandler.getNode("prices", "outpostCreationPrice").getDouble(),
				TextColors.GOLD, "\n", LanguageHandler.LD, TextColors.GRAY, " - ", TextColors.YELLOW, ConfigHandler.getNode("prices", "upkeepPerCitizen").getDouble(),
				TextColors.GOLD, "\n", LanguageHandler.LE, TextColors.GRAY, " - ", TextColors.YELLOW, ConfigHandler.getNode("prices", "blockClaimPrice").getDouble(),
				TextColors.GOLD, "\n", LanguageHandler.LF, TextColors.GRAY, " - ", TextColors.YELLOW, ConfigHandler.getNode("prices", "extraPrice").getDouble()));
		return CommandResult.success();
	}
}
