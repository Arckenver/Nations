package com.arckenver.nations.cmdexecutor.nation;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.event.PlayerTeleportEvent;
import com.arckenver.nations.object.Nation;

public class NationHomeExecutor implements CommandExecutor
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

			Location<World> spawn = nation.getSpawn("home");
			if (spawn == null)
			{
				src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.HV));
				return CommandResult.success();
			}
			if (player.hasPermission("nations.bypass.teleport.warmup")) {
				PlayerTeleportEvent event = new PlayerTeleportEvent(player, spawn, NationsPlugin.getCause());
				Sponge.getEventManager().post(event);
				if (!event.isCancelled())
				{
					player.setLocation(spawn);
					src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.GC));
				}

				return CommandResult.success();
			}
			
			src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.HU));
			
			Scheduler scheduler = Sponge.getScheduler();
			Task.Builder taskBuilder = scheduler.createTaskBuilder();
			taskBuilder.execute(new Consumer<Task>() {
				
				@Override
				public void accept(Task t) {
					t.cancel();
					PlayerTeleportEvent event = new PlayerTeleportEvent(player, spawn, NationsPlugin.getCause());
					Sponge.getEventManager().post(event);
				    //
                    if (!event.isCancelled())
					{
						player.setLocation(spawn);
						src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.GC));
					}
					//
				}
			}).delay(10, TimeUnit.SECONDS).submit(NationsPlugin.getInstance());
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
