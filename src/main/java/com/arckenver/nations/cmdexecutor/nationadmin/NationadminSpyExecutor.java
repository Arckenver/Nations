package com.arckenver.nations.cmdexecutor.nationadmin;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.channel.NationMessageChannel;

public class NationadminSpyExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.spy")
				.arguments()
				.executor(new NationadminSpyExecutor())
				.build(), "spy", "spychat");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			NationMessageChannel channel = DataHandler.getSpyChannel();
			if (channel.getMembers().contains(src))
			{
				channel.removeMember(src);
				src.sendMessage(Text.of(TextColors.YELLOW, LanguageHandler.INFO_NATIONSPY_OFF));
			}
			else
			{
				channel.addMember(src);
				src.sendMessage(Text.of(TextColors.YELLOW, LanguageHandler.INFO_NATIONSPY_ON));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
