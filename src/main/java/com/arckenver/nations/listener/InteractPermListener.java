package com.arckenver.nations.listener;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;

public class InteractPermListener
{
	@Listener(order=Order.FIRST)
	public void onInteract(InteractBlockEvent event, @First Player player)
	{
		if (!ConfigHandler.getNode("worlds").getNode(player.getWorld().getName()).getNode("enabled").getBoolean())
		{
			return;
		}
		if (player.hasPermission("nations.admin.bypass.perm.interact"))
		{
			return;
		}
		event.getTargetBlock().getLocation().ifPresent(loc -> {
			if (!DataHandler.getPerm("interact", player.getUniqueId(), loc))
			{
				event.setCancelled(true);
				player.sendMessage(Text.of(TextColors.RED, LanguageHandler.HI));
			}
		});
	}

	@Listener(order=Order.FIRST)
	public void onInteract(InteractEntityEvent event, @First Player player)
	{
		if (!ConfigHandler.getNode("worlds").getNode(player.getWorld().getName()).getNode("enabled").getBoolean())
		{
			return;
		}
		if (player.hasPermission("nations.admin.bypass.perm.interact"))
		{
			return;
		}
		Entity target = event.getTargetEntity();
		if (target instanceof Player || target instanceof Monster)
		{
			return;
		}
		if (target instanceof ItemFrame || target instanceof ArmorStand)
		{
			if (player.hasPermission("nations.admin.bypass.perm.build"))
			{
				return;
			}
			if (!DataHandler.getPerm("build", player.getUniqueId(), event.getTargetEntity().getLocation()))
			{
				event.setCancelled(true);
				player.sendMessage(Text.of(TextColors.RED, LanguageHandler.HH));
			}
			return;
		}
		if (!DataHandler.getPerm("interact", player.getUniqueId(), event.getTargetEntity().getLocation()))
		{
			event.setCancelled(true);
			player.sendMessage(Text.of(TextColors.RED, LanguageHandler.HI));
		}
	}
}
