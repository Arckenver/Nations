package com.arckenver.nations.cmdexecutor.nationadmin;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.cmdelement.NationNameElement;
import com.arckenver.nations.object.Nation;
import com.google.common.collect.ImmutableMap;

public class NationadminPermExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.perm")
				.arguments(
						GenericArguments.optional(new NationNameElement(Text.of("nation"))),
						GenericArguments.optional(GenericArguments.choices(Text.of("type"),
								ImmutableMap.<String, String> builder()
										.put(Nation.TYPE_OUTSIDER, Nation.TYPE_OUTSIDER)
										.put(Nation.TYPE_CITIZEN, Nation.TYPE_CITIZEN)
										.put(Nation.TYPE_COOWNER, Nation.TYPE_COOWNER)
										.build())),
						GenericArguments.optional(GenericArguments.choices(Text.of("perm"),
								ImmutableMap.<String, String> builder()
										.put(Nation.PERM_BUILD, Nation.PERM_BUILD)
										.put(Nation.PERM_INTERACT, Nation.PERM_INTERACT)
										.build())),
						GenericArguments.optional(GenericArguments.bool(Text.of("bool"))))
				.executor(new NationadminPermExecutor())
				.build(), "perm");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("nation").isPresent() || !ctx.<String>getOne("type").isPresent() || !ctx.<String>getOne("perm").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na perm <nation> <type> <perm> [true|false]"));
			return CommandResult.success();
		}
		String nationName = ctx.<String>getOne("nation").get();
		Nation nation = DataHandler.getNation(nationName);
		if (nation == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADNATIONNAME));
			return CommandResult.success();
		}
		String type = ctx.<String>getOne("type").get();
		String perm = ctx.<String>getOne("perm").get();
		boolean bool = (ctx.<Boolean>getOne("bool").isPresent()) ? ctx.<Boolean>getOne("bool").get() : !nation.getPerm(type, perm);
		nation.setPerm(type, perm, bool);
		DataHandler.saveNation(nation.getUUID());
		src.sendMessage(Utils.formatNationDescription(nation, Utils.CLICKER_ADMIN));
		return CommandResult.success();
	}
}
