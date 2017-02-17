package com.arckenver.nations.cmdexecutor.nation;

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
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;

public class NationInfoExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		int clicker = Utils.CLICKER_NONE;
		Nation nation;
		if (ctx.<String>getOne("nation").isPresent())
		{
			nation = DataHandler.getNation(ctx.<String>getOne("nation").get());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CB));
				return CommandResult.success();
			}
			if (src instanceof Player)
			{
				Player player = (Player) src;
				if (nation.isStaff(player.getUniqueId()))
				{
					clicker = Utils.CLICKER_DEFAULT;
				}
			}
		}
		else
		{
			if (src instanceof Player)
			{
				Player player = (Player) src;
				nation = DataHandler.getNationOfPlayer(player.getUniqueId());
				if (nation == null)
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CG));
					return CommandResult.success();
				}
				if (nation.isStaff(player.getUniqueId()))
				{
					clicker = Utils.CLICKER_DEFAULT;
				}
			}
			else
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
				return CommandResult.success();
			}
		}
		if (clicker == Utils.CLICKER_NONE && src.hasPermission("nations.command.nationadmin"))
		{
			clicker = Utils.CLICKER_ADMIN;
		}
		src.sendMessage(Utils.formatNationDescription(nation, clicker));
		return CommandResult.success();
	}
}
