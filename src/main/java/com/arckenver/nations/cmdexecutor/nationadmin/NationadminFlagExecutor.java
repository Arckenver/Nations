package com.arckenver.nations.cmdexecutor.nationadmin;

import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.cmdelement.NationNameElement;
import com.arckenver.nations.object.Nation;

public class NationadminFlagExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.flag")
				.arguments(
						GenericArguments.optional(new NationNameElement(Text.of("nation"))),
						GenericArguments.optional(GenericArguments.choices(Text.of("flag"), ConfigHandler.getNode("nations", "flags")
								.getChildrenMap()
								.keySet()
								.stream()
								.map(key -> key.toString())
								.collect(Collectors.toMap(flag -> flag, flag -> flag)))),
						GenericArguments.optional(GenericArguments.bool(Text.of("bool"))))
				.executor(new NationadminFlagExecutor())
				.build(), "flag");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("nation").isPresent() || !ctx.<String>getOne("flag").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na flag <nation> <flag> [true|false]"));
			return CommandResult.success();
		}
		String nationName = ctx.<String>getOne("nation").get();
		Nation nation = DataHandler.getNation(nationName);
		if (nation == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADNATIONNAME));
			return CommandResult.success();
		}
		String flag = ctx.<String>getOne("flag").get();
		boolean bool = (ctx.<Boolean>getOne("bool").isPresent()) ? ctx.<Boolean>getOne("bool").get() : !nation.getFlag(flag);
		nation.setFlag(flag, bool);
		DataHandler.saveNation(nation.getUUID());
		src.sendMessage(Utils.formatNationDescription(nation, Utils.CLICKER_ADMIN));
		return CommandResult.success();
	}
}
