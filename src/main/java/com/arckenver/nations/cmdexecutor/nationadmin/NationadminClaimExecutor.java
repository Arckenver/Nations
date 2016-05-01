package com.arckenver.nations.cmdexecutor.nationadmin;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Point;
import com.arckenver.nations.object.Rect;

public class NationadminClaimExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("nation").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na claim <nation>"));
			return CommandResult.success();
		}
		if (src instanceof Player)
		{
			Player player = (Player) src;
			String nationName = ctx.<String>getOne("nation").get();
			Nation nation = DataHandler.getNation(nationName);
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CB));
				return CommandResult.success();
			}
			Point a = DataHandler.getFirstPoint(player.getUniqueId());
			Point b = DataHandler.getSecondPoint(player.getUniqueId());
			if (a == null || b == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EA));
				return CommandResult.success();
			}
			if (!ConfigHandler.getNode("worlds").getNode(a.getWorld().getName()).getNode("enabled").getBoolean())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CS));
				return CommandResult.success();
			}
			Rect rect = new Rect(a, b);
			
			if (!DataHandler.canClaim(rect, nation.getUUID()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EI));
				return CommandResult.success();
			}
			
			nation.getRegion().addRect(rect);
			DataHandler.addToWorldChunks(nation);
			DataHandler.saveNation(nation.getUUID());
			src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.HL));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
