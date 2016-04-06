package com.arckenver.nations.cmdexecutor;

import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.object.Nation;

public class NationLeaveExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CI));
				return CommandResult.success();
			}
			if (nation.isPresident(player.getUniqueId()))
			{
				if (nation.getNumCitizens() > 1)
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.FL));
					return CommandResult.success();
				}
				nation.removeCitizen(player.getUniqueId());
				src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.FM));
				DataHandler.removeNation(nation.getUUID());
				MessageChannel.TO_ALL.send(Text.of(TextColors.AQUA, LanguageHandler.CN.replaceAll("\\{NATION\\}", nation.getName())));
				return CommandResult.success();
			}
			nation.removeCitizen(player.getUniqueId());
			DataHandler.saveNation(nation.getUUID());
			src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.FM));
			for (UUID citizen : nation.getCitizens())
			{
				Sponge.getServer().getPlayer(citizen).ifPresent(
						p -> p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.FN.replaceAll("\\{PLAYER\\}", player.getName()))));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
