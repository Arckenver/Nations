package com.arckenver.nations.cmdexecutor;

import java.util.Iterator;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;

public class NationListExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Builder builder = Text.builder();
		Iterator<Nation> iter = DataHandler.getNations().values().iterator();
		if (!iter.hasNext())
		{
			builder.append(Text.of(TextColors.YELLOW, LanguageHandler.CO));
		}
		else
		{
			builder.append(Text.of(TextColors.GOLD, "--------{ ", TextColors.YELLOW, LanguageHandler.JB, TextColors.GOLD, " }--------\n"));
			while (iter.hasNext())
			{
				Nation nation = iter.next();
				builder
				.append(Utils.nationClickable(TextColors.YELLOW, nation.getName()))
				.append(Text.of(TextColors.GOLD, " [" + nation.getNumCitizens() + "]"));
				if (iter.hasNext())
				{
					builder.append(Text.of(TextColors.YELLOW, ", "));
				}
			}
		}
		src.sendMessage(builder.build());
		return CommandResult.success();
	}
}
