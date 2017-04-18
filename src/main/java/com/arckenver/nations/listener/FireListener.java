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
	@Listener(order=Order.EARLY)
	public void onFire(ChangeBlockEvent event)
	{
		World world = event.getTransactions().get(0).getFinal().getLocation().get().getExtent();

		if (!ConfigHandler.getNode("worlds").getNode(world.getName()).getNode("enabled").getBoolean())
		{
			return;
		}
		event
		.getTransactions()
		.stream()
		.filter(trans -> trans.getFinal().getState().getType() == BlockTypes.FIRE)
		.filter(trans -> {
			Optional<Location<World>> optLoc = trans.getFinal().getLocation();
			if (!optLoc.isPresent())
				return false;
			return !DataHandler.getFlag("fire", optLoc.get());
		})
		.forEach(trans -> trans.setValid(false));
	}
}
