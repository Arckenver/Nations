package com.arckenver.nations.listener;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;

public class BuildPermListener
{

	@Listener(order=Order.FIRST, beforeModifications = true)
	public void onPlayerChangeBlock(ChangeBlockEvent.Pre event, @First Player player)
	{
		if (player.hasPermission("nations.admin.bypass.perm.build"))
		{
			return;
		}
		for (Location<World> loc : event.getLocations()) {
			if (ConfigHandler.getNode("worlds").getNode(loc.getExtent().getName()).getNode("enabled").getBoolean()
					&& !ConfigHandler.isWhitelisted("break", loc.getBlock().getId())
					&& !DataHandler.getPerm("build", player.getUniqueId(), loc))
			{
				event.setCancelled(true);
				try {
					player.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_BUILD));
				} catch (Exception e) {}
				return;
			}
		}
	}

	@Listener(order=Order.FIRST, beforeModifications = true)
	public void onPlayerPlacesBlock(ChangeBlockEvent.Place event, @First Player player)
	{
		if (player.hasPermission("nations.admin.bypass.perm.build"))
		{
			return;
		}
		event
		.getTransactions()
		.stream()
		.forEach(trans -> trans.getOriginal().getLocation().ifPresent(loc -> {
			if (ConfigHandler.getNode("worlds").getNode(trans.getFinal().getLocation().get().getExtent().getName()).getNode("enabled").getBoolean()
					&& !ConfigHandler.isWhitelisted("build", trans.getFinal().getState().getType().getId())
					&& !DataHandler.getPerm("build", player.getUniqueId(), loc))
			{
				trans.setValid(false);
				try {
					player.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_BUILD));
				} catch (Exception e) {}
			}
		}));
	}

	@Listener(order=Order.FIRST, beforeModifications = true)
	public void onPlayerBreaksBlock(ChangeBlockEvent.Break event, @First Player player)
	{
		if (player.hasPermission("nations.admin.bypass.perm.build"))
		{
			return;
		}
		event
		.getTransactions()
		.stream()
		.forEach(trans -> trans.getOriginal().getLocation().ifPresent(loc -> {
			if (!ConfigHandler.isWhitelisted("break", trans.getFinal().getState().getType().getId())
					&& ConfigHandler.getNode("worlds").getNode(trans.getFinal().getLocation().get().getExtent().getName()).getNode("enabled").getBoolean()
					&& !DataHandler.getPerm("build", player.getUniqueId(), loc))
			{
				trans.setValid(false);
				try {
					player.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_BUILD));
				} catch (Exception e) {}
			}
		}));
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onSignChanged(ChangeSignEvent event, @First User player)
	{
		if (!ConfigHandler.getNode("worlds").getNode(event.getTargetTile().getLocation().getExtent().getName()).getNode("enabled").getBoolean())
		{
			return;
		}
		if (player.hasPermission("nations.admin.bypass.perm.build"))
		{
			return;
		}
		if (!DataHandler.getPerm("build", player.getUniqueId(), event.getTargetTile().getLocation()))
		{
			event.setCancelled(true);
		}
	}

	@Listener(order=Order.FIRST, beforeModifications = true)
	public void onEntitySpawn(SpawnEntityEvent event, @First Player player)
	{
		if (!ConfigHandler.getNode("worlds").getNode(event.getEntities().get(0).getWorld().getName()).getNode("enabled").getBoolean())
		{
			return;
		}
		if (player.hasPermission("nations.admin.bypass.perm.build"))
		{
			return;
		}
		if (event.getCause().contains(SpawnTypes.PLACEMENT))
		{
			try {
				if (!DataHandler.getPerm("build", player.getUniqueId(), event.getEntities().get(0).getLocation()))
					event.setCancelled(true);
			} catch (IndexOutOfBoundsException e) {}
		}
	}
}
