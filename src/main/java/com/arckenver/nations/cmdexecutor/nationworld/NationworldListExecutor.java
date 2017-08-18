package com.arckenver.nations.cmdexecutor.nationworld;

import java.util.Iterator;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;

public class NationworldListExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationworld.list")
				.arguments()
				.executor(new NationworldListExecutor())
				.build(), "list");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Builder builder = Text.builder();
		Iterator<World> iter = Sponge.getServer().getWorlds().iterator();
		builder.append(Text.of(TextColors.GOLD, "--------{ ", TextColors.YELLOW, LanguageHandler.HEADER_WORLDLIST, TextColors.GOLD, " }--------\n"));
		while (iter.hasNext())
		{
			World world = iter.next();
			builder.append(Utils.worldClickable(TextColors.YELLOW, world.getName()));
			if (iter.hasNext())
			{
				builder.append(Text.of(TextColors.YELLOW, ", "));
			}
		}
		if (src.hasPermission("nations.command.nationworld.info"))
		{
			builder.append(Text.of(TextColors.DARK_GRAY, " <- click"));
		}
		src.sendMessage(builder.build());
		return CommandResult.success();
	}
}
