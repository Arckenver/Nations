package com.arckenver.nations.cmdexecutor.nation;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;

public class NationCitizenExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("player").isPresent())
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NEEDPLAYERNAME));
			return CommandResult.success();
		}
		String name = ctx.<String>getOne("player").get();
		src.sendMessage(Utils.formatCitizenDescription(name));
		return CommandResult.success();
	}
}
