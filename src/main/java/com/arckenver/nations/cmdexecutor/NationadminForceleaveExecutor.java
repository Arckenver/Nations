package com.arckenver.nations.cmdexecutor;

import java.util.UUID;

import org.spongepowered.api.Sponge;
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

public class NationadminForceleaveExecutor implements CommandExecutor
{
	@Override
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("player").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na forceleave <player>"));
			return CommandResult.success();
		}
		String playerName = ctx.<String>getOne("player").get();
		
		UUID uuid = DataHandler.getPlayerUUID(playerName);
		if (uuid == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CC));
			return CommandResult.success();
		}
		Nation nation = DataHandler.getNationOfPlayer(uuid);
		if (nation == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.HJ));
			return CommandResult.success();
		}
		if (nation.isPresident(uuid))
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.HK));
			return CommandResult.success();
		}
		nation.removeCitizen(uuid);
		DataHandler.saveNation(nation.getUUID());
		Sponge.getServer().getPlayer(uuid).ifPresent(p -> 
			p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.FM)));
		src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.HL));
		return CommandResult.success();
	}
}
