package com.arckenver.nations.cmdexecutor.nationadmin;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.LanguageHandler;

public class NationadminReloadExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.reload")
				.arguments()
				.executor(new NationadminReloadExecutor())
				.build(), "reload");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		LanguageHandler.load();
		ConfigHandler.load(src);
		return CommandResult.success();
	}
}
