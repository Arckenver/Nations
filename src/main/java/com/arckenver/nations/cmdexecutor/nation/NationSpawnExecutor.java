package com.arckenver.nations.cmdexecutor.nation;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.Utils;
import com.arckenver.nations.event.PlayerTeleportEvent;
import com.arckenver.nations.object.Nation;

public class NationSpawnExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CI));
				return CommandResult.success();
			}
			if (!ctx.<String>getOne("name").isPresent())
			{
				src.sendMessage(Text.builder()
						.append(Text.of(TextColors.AQUA, LanguageHandler.GA.split("\\{SPAWNLIST\\}")[0]))
						.append(Utils.formatNationSpawns(nation, TextColors.YELLOW))
						.append(Text.of(TextColors.AQUA, LanguageHandler.GA.split("\\{SPAWNLIST\\}")[1]))
						.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX)).build());
				return CommandResult.success();
			}
			String spawnName = ctx.<String>getOne("name").get();
			Location<World> spawn = nation.getSpawn(spawnName);
			if (spawn == null)
			{
				src.sendMessage(Text.builder()
						.append(Text.of(TextColors.RED, LanguageHandler.GB.split("\\{SPAWNLIST\\}")[0]))
						.append(Utils.formatNationSpawns(nation, TextColors.YELLOW))
						.append(Text.of(TextColors.RED, LanguageHandler.GB.split("\\{SPAWNLIST\\}")[1]))
						.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX)).build());
				return CommandResult.success();
			}
			PlayerTeleportEvent event = new PlayerTeleportEvent(player, spawn, NationsPlugin.getCause());
			Sponge.getEventManager().post(event);
			if (!event.isCancelled())
			{
				player.setLocation(spawn);
				src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.GC));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
