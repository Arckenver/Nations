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
import com.arckenver.nations.cmdelement.NationNameElement;
import com.arckenver.nations.object.Nation;
import com.google.common.collect.ImmutableMap;

public class NationadminExtraExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.extra")
				.arguments(
						GenericArguments.optional(GenericArguments.choices(Text.of("give|take|set"),
								ImmutableMap.<String, String> builder()
										.put("give", "give")
										.put("take", "take")
										.put("set", "set")
										.build())),
						GenericArguments.optional(new NationNameElement(Text.of("nation"))),
						GenericArguments.optional(GenericArguments.integer(Text.of("amount"))))
				.executor(new NationadminExtraExecutor())
				.build(), "extra");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("nation").isPresent() || !ctx.<String>getOne("give|take|set").isPresent() || !ctx.<String>getOne("amount").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na extra <give|take|set> <nation> <amount>"));
			return CommandResult.success();
		}
		String nationName = ctx.<String>getOne("nation").get();
		Integer amount = Integer.valueOf(ctx.<Integer>getOne("amount").get());
		String operation = ctx.<String>getOne("give|take|set").get();
		
		Nation nation = DataHandler.getNation(nationName);
		if (nation == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADNATIONNAME));
			return CommandResult.success();
		}
		if (operation.equalsIgnoreCase("give"))
		{
			nation.addExtras(amount);
		}
		else if (operation.equalsIgnoreCase("take"))
		{
			nation.removeExtras(amount);
		}
		else if (operation.equalsIgnoreCase("set"))
		{
			nation.setExtras(amount);
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADARG_GTS));
			return CommandResult.success();
		}
		DataHandler.saveNation(nation.getUUID());
		src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.SUCCESS_GENERAL));
		return CommandResult.success();
	}
}
