package com.arckenver.nations.cmdexecutor.nationworld;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;
import com.google.common.collect.ImmutableMap;

public class NationworldPermExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationworld.perm")
				.arguments(
						GenericArguments.choices(Text.of("perm"),
								ImmutableMap.<String, String> builder()
										.put(Nation.PERM_BUILD, Nation.PERM_BUILD)
										.put(Nation.PERM_INTERACT, Nation.PERM_INTERACT)
										.build()),
						GenericArguments.optional(GenericArguments.bool(Text.of("bool"))))
				.executor(new NationworldPermExecutor())
				.build(), "perm");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		String worldName;
		if (ctx.<String>getOne("world").isPresent())
		{
			worldName = ctx.<String>getOne("world").get();
			if (!Sponge.getServer().getWorld(worldName).isPresent())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADWORLDNAME));
				return CommandResult.success();
			}
		}
		else
		{
			if (src instanceof Player)
			{
				Player player = (Player) src;
				worldName = player.getWorld().getName();
			}
			else
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NEEDWORLDNAME));
				return CommandResult.success();
			}
		}
		String perm = ctx.<String>getOne("perm").get();
		boolean currentVal = ConfigHandler.getNode("worlds").getNode(worldName).getNode("perms").getNode(perm).getBoolean();
		boolean bool = (ctx.<Boolean>getOne("bool").isPresent()) ? ctx.<Boolean>getOne("bool").get() : !currentVal;
		ConfigHandler.getNode("worlds").getNode(worldName).getNode("perms").getNode(perm).setValue(bool);
		ConfigHandler.save();
		src.sendMessage(Utils.formatWorldDescription(worldName));
		return CommandResult.success();
	}
}
