package com.arckenver.nations.cmdexecutor.zone;

import java.util.Map.Entry;
import java.util.UUID;

import org.spongepowered.api.Sponge;
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
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Point;
import com.arckenver.nations.object.Rect;
import com.arckenver.nations.object.Zone;

public class ZoneCreateExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			Nation nation = DataHandler.getNation(player.getLocation());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.DQ));
				return CommandResult.success();
			}
			if (!nation.isStaff(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CK));
				return CommandResult.success();
			}
			String zoneName = null;
			if (ctx.<String>getOne("name").isPresent())
			{
				zoneName = ctx.<String>getOne("name").get();
			}
			if (zoneName != null && !zoneName.matches("[\\p{Alnum}\\p{IsIdeographic}\\p{IsLetter}\"_\"]*{1,30}"))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.FY
						.replaceAll("\\{MIN\\}", "1")
						.replaceAll("\\{MAX\\}", "30")));
				return CommandResult.success();
			}
			UUID owner = null;
			if (ctx.<String>getOne("owner").isPresent())
			{
				owner = DataHandler.getPlayerUUID(ctx.<String>getOne("owner").get());
				if (owner == null)
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CC));
					return CommandResult.success();
				}
			}
			Point a = DataHandler.getFirstPoint(player.getUniqueId());
			Point b = DataHandler.getSecondPoint(player.getUniqueId());
			if (a == null || b == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EA));
				return CommandResult.success();
			}
			Rect rect = new Rect(a, b);
			if (!nation.getRegion().isInside(rect))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.HG));
				return CommandResult.success();
			}
			for (Zone zone : nation.getZones().values())
			{
				if (zoneName != null && zone.isNamed() && zone.getName().equalsIgnoreCase(zoneName))
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.GR));
					return CommandResult.success();
				}
				if (rect.intersects(zone.getRect()))
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.GS));
					return CommandResult.success();
				}
			}
			Zone zone = new Zone(UUID.randomUUID(), zoneName, rect, owner);
			for (Entry<String, Boolean> e : nation.getFlags().entrySet())
			{
				zone.setFlag(e.getKey(), e.getValue());
			}
			nation.addZone(zone);
			DataHandler.saveNation(nation.getUUID());
			src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.GT.replaceAll("\\{ZONE\\}", zone.getName())));
			Sponge.getServer().getPlayer(owner).ifPresent(
					p -> p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.GU.replaceAll("\\{ZONE\\}", zone.getName()))));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
