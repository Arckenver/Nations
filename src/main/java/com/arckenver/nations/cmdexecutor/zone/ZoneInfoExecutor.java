package com.arckenver.nations.cmdexecutor.zone;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Zone;

public class ZoneInfoExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.info")
				.arguments(GenericArguments.optional(GenericArguments.string(Text.of("zone"))))
				.executor(new ZoneInfoExecutor())
				.build(), "info");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			Nation nation = DataHandler.getNation(player.getLocation());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NEEDSTANDNATION));
				return CommandResult.success();
			}
			Zone zone = null;
			if (!ctx.<String>getOne("zone").isPresent())
			{
				zone = nation.getZone(player.getLocation());
				if (zone == null)
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NEEDZONE));
					return CommandResult.success();
				}
			}
			else
			{
				String zoneName = ctx.<String>getOne("zone").get();
				for (Zone z : nation.getZones().values())
				{
					if (z.isNamed() && z.getRealName().equalsIgnoreCase(zoneName))
					{
						zone = z;
					}
				}
				if (zone == null)
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADZONENAME));
					return CommandResult.success();
				}
			}
			int clicker = Utils.CLICKER_DEFAULT;
			if (zone.isOwner(player.getUniqueId()) || zone.isCoowner(player.getUniqueId()) || nation.isStaff(player.getUniqueId()))
			{
				clicker = Utils.CLICKER_DEFAULT;
			}
			src.sendMessage(Utils.formatZoneDescription(zone, nation, clicker));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
