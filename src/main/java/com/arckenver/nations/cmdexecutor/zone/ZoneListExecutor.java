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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.cmdelement.NationNameElement;
import com.arckenver.nations.object.Nation;

public class ZoneListExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.list")
				.arguments(GenericArguments.optional(new NationNameElement(Text.of("nation"))))
				.executor(new ZoneListExecutor())
				.build(), "list");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Nation nation;
		if (ctx.<String>getOne("nation").isPresent())
		{
			if (!src.hasPermission("nations.admin.zone.listall"))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_LISTZONES));
				return CommandResult.success();
			}
			nation = DataHandler.getNation(ctx.<String>getOne("nation").get());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADNATIONNAME));
				return CommandResult.success();
			}
		}
		else
		{
			if (src instanceof Player)
			{
				Player player = (Player) src;
				nation = DataHandler.getNation(player.getLocation());
				if (nation == null)
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NEEDNATIONNAME));
					return CommandResult.success();
				}
			}
			else
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NEEDNATIONNAME));
				return CommandResult.success();
			}
		}
		
		String str = LanguageHandler.HEADER_ZONELIST.replaceAll("\\{NATION\\}", nation.getName());
		String[] splited = str.split("\\{ZONELIST\\}");
		src.sendMessage(Utils.structureX(
				nation.getZones().values().iterator(),
				Text.builder(splited[0]).color(TextColors.AQUA), 
				(b) -> b.append(Text.of(TextColors.GRAY, LanguageHandler.FORMAT_NONE)),
				(b, zone) -> b.append(Text.builder(zone.getName()).color(TextColors.YELLOW).onClick(TextActions.runCommand("/z info " + zone.getRealName())).build()),
				(b) -> b.append(Text.of(TextColors.AQUA, ", "))).append(Text.of(TextColors.AQUA, (splited.length > 1) ? splited[1] : "")).build());
		
		return CommandResult.success();
	}
}
