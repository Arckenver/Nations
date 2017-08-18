package com.arckenver.nations.listener;

import java.util.Optional;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;

public class FireListener
{
	@Listener(order=Order.EARLY, beforeModifications = true)
	public void onFire(ChangeBlockEvent event)
	{
		event
		.getTransactions()
		.stream()
		.filter(trans -> trans.getFinal().getState().getType() == BlockTypes.FIRE)
		.filter(trans -> {
			Optional<Location<World>> optLoc = trans.getFinal().getLocation();
			if (!optLoc.isPresent())
				return false;
			if(!ConfigHandler.getNode("worlds").getNode(optLoc.get().getExtent().getName()).getNode("enabled").getBoolean()){
				return false;
			}
			return !DataHandler.getFlag("fire", optLoc.get());
		})
		.forEach(trans -> trans.setValid(false));
	}
}
