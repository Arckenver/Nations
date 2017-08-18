package com.arckenver.nations.cmdexecutor.zone;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.LanguageHandler;

public class ZoneExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.help")
				.arguments()
				.executor(new ZoneExecutor())
				.build(), "help", "?");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		src.sendMessage(Text.of(
				TextColors.GOLD, ((src instanceof Player) ? "" : "\n") + "--------{ ",
				TextColors.YELLOW, "/zone",
				TextColors.GOLD, " }--------",
				TextColors.GOLD, "\n/z info [zone]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_Z_INFO,
				TextColors.GOLD, "\n/z list", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_Z_LIST,
				TextColors.GOLD, "\n/z create <name> [owner]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_Z_CREATE,
				TextColors.GOLD, "\n/z delete [zone]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_Z_DELETE,
				TextColors.GOLD, "\n/z coowner <add/remove> <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_Z_COOWNER,
				TextColors.GOLD, "\n/z setowner <player>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_Z_SETOWNER,
				TextColors.GOLD, "\n/z delowner", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_Z_DELOWNER,
				TextColors.GOLD, "\n/z rename", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_Z_RENAME,
				TextColors.GOLD, "\n/z perm <type> <perm> [true/false]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_Z_PERM,
				TextColors.GOLD, "\n/z flag <flag> [true/false]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_Z_FLAG,
				TextColors.GOLD, "\n/z sell <price>", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_Z_SELL,
				TextColors.GOLD, "\n/z buy", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.HELP_DESC_CMD_Z_BUY));
		return CommandResult.success();
	}
}
