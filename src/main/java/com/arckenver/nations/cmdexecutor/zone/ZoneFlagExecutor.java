package com.arckenver.nations.cmdexecutor.zone;

import java.util.stream.Collectors;

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

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Zone;

public class ZoneFlagExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.flag")
				.arguments(
						GenericArguments.choices(Text.of("flag"), ConfigHandler.getNode("nations", "flags")
								.getChildrenMap()
								.keySet()
								.stream()
								.map(o -> o.toString())
								.collect(Collectors.toMap(flag -> flag, flag -> flag))),
						GenericArguments.optional(GenericArguments.bool(Text.of("bool"))))
				.executor(new ZoneFlagExecutor())
				.build(), "flag");
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
			Zone zone = nation.getZone(player.getLocation());
			if (zone == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NEEDSTANDZONESELF));
				return CommandResult.success();
			}
			if (!zone.isOwner(player.getUniqueId()) && !nation.isStaff(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOOWNER));
				return CommandResult.success();
			}
			String flag = ctx.<String>getOne("flag").get();
			boolean bool = (ctx.<Boolean>getOne("bool").isPresent()) ? ctx.<Boolean>getOne("bool").get() : !zone.getFlag(flag);
			zone.setFlag(flag, bool);
			DataHandler.saveNation(nation.getUUID());
			src.sendMessage(Utils.formatZoneDescription(zone, nation, Utils.CLICKER_DEFAULT));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
