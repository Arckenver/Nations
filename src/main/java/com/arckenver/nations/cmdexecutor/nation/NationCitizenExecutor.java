package com.arckenver.nations.cmdexecutor.nation;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.cmdelement.PlayerNameElement;

public class NationCitizenExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.citizen")
				.arguments(GenericArguments.optional(new PlayerNameElement(Text.of("player"))))
				.executor(new NationCitizenExecutor())
				.build(), "citizen", "whois");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("player").isPresent())
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NEEDPLAYERNAME));
			return CommandResult.success();
		}
		String name = ctx.<String>getOne("player").get();
		src.sendMessage(Utils.formatCitizenDescription(name));
		return CommandResult.success();
	}
}
