package com.arckenver.nations.cmdexecutor.nationadmin;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.object.Nation;

public class NationadminSetnameExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String> getOne("oldname").isPresent() || !ctx.<String> getOne("newname").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na setname <oldname> <newname>"));
			return CommandResult.success();
		}
		String oldName = ctx.<String> getOne("oldname").get();
		String newName = ctx.<String> getOne("newname").get();
		Nation nation = DataHandler.getNation(oldName);
		if (nation == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CI));
			return CommandResult.success();
		}
		if (DataHandler.getNation(newName) != null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EL));
			return CommandResult.success();
		}
		if (!newName.matches("[\\p{Alnum}\\p{IsIdeographic}\\p{IsLetter}\"_\"]*"))
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EM));
			return CommandResult.success();
		}
		if (newName.length() < ConfigHandler.getNode("others", "minNationNameLength").getInt()
				|| newName.length() > ConfigHandler.getNode("others", "maxNationNameLength").getInt())
		{
			src.sendMessage(Text.of(TextColors.RED,
					LanguageHandler.EN
							.replaceAll("\\{MIN\\}",
									ConfigHandler.getNode("others", "minNationNameLength").getString())
							.replaceAll("\\{MAX\\}",
									ConfigHandler.getNode("others", "maxNationNameLength").getString())));
			return CommandResult.success();
		}
		nation.setName(newName);
		DataHandler.saveNation(nation.getUUID());
		MessageChannel.TO_ALL.send(Text.of(TextColors.RED,
				LanguageHandler.FW.replaceAll("\\{OLDNAME\\}", oldName).replaceAll("\\{NEWNAME\\}", nation.getName())));
		return CommandResult.success();
	}
}

