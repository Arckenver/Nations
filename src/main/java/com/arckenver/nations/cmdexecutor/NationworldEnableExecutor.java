package com.arckenver.nations.cmdexecutor;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class NationworldEnableExecutor implements CommandExecutor
{
	@Override
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		String worldName;
		if (ctx.<String>getOne("world").isPresent())
		{
			worldName = ctx.<String>getOne("world").get();
			if (!Sponge.getServer().getWorld(worldName).isPresent())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CT));
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
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CU));
				return CommandResult.success();
			}
		}
		CommentedConfigurationNode node = ConfigHandler.getNode("worlds").getNode(worldName);
		if (node.getNode("enabled").getBoolean())
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CV));
			return CommandResult.success();
		}
		
		node.getNode("enabled").setValue(true);
		
		node.getNode("perms").getNode(Nation.PERM_BUILD).setValue(true);
		node.getNode("perms").getNode(Nation.PERM_INTERACT).setValue(true);
		
		node.getNode("flags").getNode("pvp").setValue(true);
		node.getNode("flags").getNode("mobs").setValue(true);
		node.getNode("flags").getNode("fire").setValue(true);
		node.getNode("flags").getNode("explosions").setValue(true);
		
		ConfigHandler.save();
		src.sendMessage(Utils.formatWorldDescription(worldName, true));
		return CommandResult.success();
	}
}
