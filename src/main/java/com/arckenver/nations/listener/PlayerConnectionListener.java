package com.arckenver.nations.listener;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import com.arckenver.nations.DataHandler;

public class PlayerConnectionListener
{
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event)
	{
		if (event.getTargetEntity() instanceof Player)
		{
			
		}
	}

	@Listener
	public void onPlayerLeave(ClientConnectionEvent.Disconnect event)
	{
		if (event.getTargetEntity() instanceof Player)
		{
			Player player = (Player) event.getTargetEntity();
			DataHandler.removeFirstPoint(player.getUniqueId());
			DataHandler.removeSecondPoint(player.getUniqueId());
		}
	}
}
