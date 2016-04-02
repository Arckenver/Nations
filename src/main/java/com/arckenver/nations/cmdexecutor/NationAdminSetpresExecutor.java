package com.arckenver.nations.cmdexecutor;

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
import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.object.Nation;

public class NationAdminSetpresExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("nation").isPresent() || !ctx.<String>getOne("president").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na setpres <nation> <president>"));
			return CommandResult.success();
		}
		String nationName = ctx.<String>getOne("nation").get();
		Nation nation = DataHandler.getNation(nationName);
		if (nation == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CB));
			return CommandResult.success();
		}
		String presidentName = ctx.<String>getOne("president").get();
		UUID presidentUUID = DataHandler.getPlayerUUID(presidentName);
		if (presidentUUID == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CC));
			return CommandResult.success();
		}
		if (nation.isPresident(presidentUUID))
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CQ));
			return CommandResult.success();
		}
		if (!nation.isCitizen(presidentUUID))
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CR));
			return CommandResult.success();
		}
		UUID oldPresidentUUID = nation.getPresident();
		final String oldPresidentName = DataHandler.getPlayerName(oldPresidentUUID);
		nation.setPresident(presidentUUID);
		DataHandler.saveNation(nation.getUUID());
		
		for (UUID citizen : nation.getCitizens())
		{
			NationsPlugin.getGame().getServer().getPlayer(citizen).ifPresent(
					p -> p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.FV
							.replaceAll("\\{SUCCESSOR\\}", presidentName)
							.replaceAll("\\{PLAYER\\}", (oldPresidentName == null) ? LanguageHandler.IQ : oldPresidentName))));
		}
		
		return CommandResult.success();
	}
}
