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
import com.google.common.collect.ImmutableMap;

public class ZonePermExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.perm")
				.arguments(
						GenericArguments.choices(Text.of("type"),
								ImmutableMap.<String, String> builder()
										.put(Nation.TYPE_OUTSIDER, Nation.TYPE_OUTSIDER)
										.put(Nation.TYPE_CITIZEN, Nation.TYPE_CITIZEN)
										.put(Nation.TYPE_COOWNER, Nation.TYPE_COOWNER)
										.build()),
						GenericArguments.choices(Text.of("perm"),
								ImmutableMap.<String, String> builder()
										.put(Nation.PERM_BUILD, Nation.PERM_BUILD)
										.put(Nation.PERM_INTERACT, Nation.PERM_INTERACT)
										.build()),
						GenericArguments.optional(GenericArguments.bool(Text.of("bool"))))
				.executor(new ZonePermExecutor())
				.build(), "perm");
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
			String type = ctx.<String>getOne("type").get();
			String perm = ctx.<String>getOne("perm").get();
			boolean bool = (ctx.<Boolean>getOne("bool").isPresent()) ? ctx.<Boolean>getOne("bool").get() : !zone.getPerm(type, perm);
			zone.setPerm(type, perm, bool);
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
