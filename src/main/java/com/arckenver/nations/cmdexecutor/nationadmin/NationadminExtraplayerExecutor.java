package com.arckenver.nations.cmdexecutor.nationadmin;

import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.object.Nation;

public class NationadminExtraplayerExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("player").isPresent() || !ctx.<String>getOne("give|take|set").isPresent() || !ctx.<String>getOne("amount").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na extraplayer <give|take|set> <player> <amount>"));
			return CommandResult.success();
		}
		String playerName = ctx.<String>getOne("player").get();
		Integer amount = Integer.valueOf(ctx.<Integer>getOne("amount").get());
		String operation = ctx.<String>getOne("give|take|set").get();
		
		UUID playerUUID = DataHandler.getPlayerUUID(playerName);
		if (playerUUID == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CC));
			return CommandResult.success();
		}
		
		Nation nation = DataHandler.getNationOfPlayer(playerUUID);
		if (nation == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.HJ));
			return CommandResult.success();
		}
		if (operation.equalsIgnoreCase("give"))
		{
			nation.addExtras(amount);
		}
		else if (operation.equalsIgnoreCase("take"))
		{
			nation.removeExtras(amount);
		}
		else if (operation.equalsIgnoreCase("set"))
		{
			nation.setExtras(amount);
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CX));
			return CommandResult.success();
		}
		DataHandler.saveNation(nation.getUUID());
		src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.HL));
		return CommandResult.success();
	}
}
