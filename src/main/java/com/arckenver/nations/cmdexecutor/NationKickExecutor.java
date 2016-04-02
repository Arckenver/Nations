package com.arckenver.nations.cmdexecutor;

import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.object.Nation;

public class NationKickExecutor implements CommandExecutor
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
			if (!nation.isPresident(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CJ));
				return CommandResult.success();
			}
			if (!ctx.<String>getOne("player").isPresent())
			{
				src.sendMessage(Text.of(TextColors.YELLOW, "/n kick <player>"));
				return CommandResult.success();
			}
			String toKick = ctx.<String>getOne("player").get();
			UUID uuid = DataHandler.getPlayerUUID(toKick);
			if (uuid == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CC));
				return CommandResult.success();
			}
			if (!nation.isCitizen(uuid))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.FF));
				return CommandResult.success();
			}
			if (player.getUniqueId().equals(uuid))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.FG));
				return CommandResult.success();
			}
			if (nation.isPresident(uuid))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.FH));
				return CommandResult.success();
			}
			if (nation.isMinister(uuid) && nation.isMinister(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.FI));
				return CommandResult.success();
			}
			nation.removeCitizen(uuid);
			DataHandler.saveNation(nation.getUUID());
			src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.FJ.replaceAll("\\{PLAYER\\}", toKick)));
			NationsPlugin.getGame().getServer().getPlayer(uuid).ifPresent(
					p -> p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.FJ.replaceAll("\\{PLAYER\\}", player.getName()))));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
