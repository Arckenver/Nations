package com.arckenver.nations.listener;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;

public class InteractPermListener
{
	@Listener
	public void onInteract(InteractBlockEvent event, @First Player player)
	{
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
}
