package com.arckenver.nations.cmdexecutor.nation;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.NationsPlugin;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class NationCostExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		src.sendMessage(Text.of(
				TextColors.GOLD, ((src instanceof Player) ? "" : "\n") + "--------{ ",
				TextColors.YELLOW, "/n cost",
				TextColors.GOLD, " }--------",
				TextColors.GOLD, "\n", LanguageHandler.LA, TextColors.GRAY, " - ", NationsPlugin.getEcoService().getDefaultCurrency().getSymbol(), TextColors.YELLOW, ConfigHandler.getNode("prices", "blockClaimPrice").getDouble(),
				TextColors.GOLD, "\n", LanguageHandler.LB, TextColors.GRAY, " - ", NationsPlugin.getEcoService().getDefaultCurrency().getSymbol(), TextColors.YELLOW, ConfigHandler.getNode("prices", "extraPrice").getDouble(),
				TextColors.GOLD, "\n", LanguageHandler.LC, TextColors.GRAY, " - ", NationsPlugin.getEcoService().getDefaultCurrency().getSymbol(), TextColors.YELLOW, ConfigHandler.getNode("prices", "nationCreationPrice").getDouble(),
				TextColors.GOLD, "\n", LanguageHandler.LD, TextColors.GRAY, " - ", NationsPlugin.getEcoService().getDefaultCurrency().getSymbol(), TextColors.YELLOW, ConfigHandler.getNode("prices", "outpostCreationPrice").getDouble(),
				TextColors.GOLD, "\n", LanguageHandler.LE, TextColors.GRAY, " - ", NationsPlugin.getEcoService().getDefaultCurrency().getSymbol(), TextColors.YELLOW, (ConfigHandler.getNode("prices", "blockClaimPrice").getDouble()) * ConfigHandler.getNode("prices", "unclaimRefundPercentage").getDouble()
		));
		if (!ConfigHandler.getNode("upkeep", "perblock").getBoolean())
				{
					src.sendMessage(Text.of(
							TextColors.GOLD, LanguageHandler.LF, TextColors.GRAY, " - ", NationsPlugin.getEcoService().getDefaultCurrency().getSymbol(), TextColors.YELLOW, ConfigHandler.getNode("upkeep", "price").getDouble()
					));
				}
				else
				{
					src.sendMessage(Text.of(
						TextColors.GOLD, LanguageHandler.LG, TextColors.GRAY, " - ", NationsPlugin.getEcoService().getDefaultCurrency().getSymbol(), TextColors.YELLOW, ConfigHandler.getNode("prices", "upkeepPerCitizen").getDouble()
					));
				}
		return CommandResult.success();
	}
}
