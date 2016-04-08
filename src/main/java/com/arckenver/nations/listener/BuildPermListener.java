package com.arckenver.nations.listener;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;

public class BuildPermListener
{
	@Listener
	public void onPlayerPlacesBlock(ChangeBlockEvent.Place event, @First Player player)
	{
		if (!ConfigHandler.getNode("worlds").getNode(event.getTargetWorld().getName()).getNode("enabled").getBoolean())
		{
			return;
		}
		if (player.hasPermission("nations.admin.bypass.perm.build"))
		{
			return;
		}
		event
		.getTransactions()
		.stream()
		.forEach(trans -> trans.getOriginal().getLocation().ifPresent(loc -> {
				trans.setValid(DataHandler.getPerm("build", player.getUniqueId(), loc));
				if(!DataHandler.getPerm("build", player.getUniqueId(), loc))
				{
					player.sendMessage(Text.of(TextColors.RED, LanguageHandler.HH));
				}
		}));
	}
	
	@Listener
	public void onPlayerBreaksBlock(ChangeBlockEvent.Break event, @First Player player)
	{
		if (!ConfigHandler.getNode("worlds").getNode(event.getTargetWorld().getName()).getNode("enabled").getBoolean())
		{
			return;
		}
		if (player.hasPermission("nations.admin.bypass.perm.build"))
		{
			return;
		}
		event
		.getTransactions()
		.stream()
		.forEach(trans -> trans.getOriginal().getLocation().ifPresent(loc -> {
				trans.setValid(DataHandler.getPerm("build", player.getUniqueId(), loc));
				if(!DataHandler.getPerm("build", player.getUniqueId(), loc))
				{
					player.sendMessage(Text.of(TextColors.RED, LanguageHandler.HH));
				}
		}));
	}
}
