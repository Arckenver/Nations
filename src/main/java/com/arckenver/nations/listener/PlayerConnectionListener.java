package com.arckenver.nations.listener;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.channel.MessageChannel;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.object.Nation;

public class PlayerConnectionListener
{
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event)
	{
		if (event.getTargetEntity() instanceof Player)
		{
			Player player = event.getTargetEntity();
			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			if (nation != null)
				nation.getMessageChannel().addMember(player);
			player.setMessageChannel(MessageChannel.TO_ALL);
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
			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			if (nation != null)
				nation.getMessageChannel().removeMember(player);
			DataHandler.getSpyChannel().removeMember(player);
		}
	}
}
