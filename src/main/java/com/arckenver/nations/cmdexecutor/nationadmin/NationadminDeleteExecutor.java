package com.arckenver.nations.cmdexecutor.nationadmin;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.cmdelement.NationNameElement;
import com.arckenver.nations.object.Nation;

public class NationadminDeleteExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.delete")
				.arguments(GenericArguments.optional(new NationNameElement(Text.of("nation"))))
				.executor(new NationadminDeleteExecutor())
				.build(), "delete");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("nation").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na delete <nation>"));
			return CommandResult.success();
		}
		String nationName = ctx.<String>getOne("nation").get();
		Nation nation = DataHandler.getNation(nationName);
		if (nation == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADNATIONNAME));
			return CommandResult.success();
		}
		DataHandler.removeNation(nation.getUUID());
		MessageChannel.TO_ALL.send(Text.of(TextColors.AQUA, LanguageHandler.INFO_NATIONFALL.replaceAll("\\{NATION\\}", nation.getName())));
		return CommandResult.success();
	}
}
