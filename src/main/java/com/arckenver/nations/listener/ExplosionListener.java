package com.arckenver.nations.listener;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.world.ExplosionEvent;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;

public class ExplosionListener
{
	@Listener(order=Order.FIRST, beforeModifications = true)
	public void onExplosion(ExplosionEvent.Pre event)
	{
		if (!ConfigHandler.getNode("worlds").getNode(event.getTargetWorld().getName()).getNode("enabled").getBoolean())
		{
			return;
		}
		if (!DataHandler.getFlag("explosions", event.getExplosion().getLocation()))
		{
			event.setCancelled(true);
			/*event.setExplosion(Sponge.getRegistry().createBuilder(Explosion.Builder.class)
					.from(event.getExplosion())
					.canCauseFire(canCauseFire)
					.shouldBreakBlocks(canBreakBlocks)
					.build());*/
		}
	}
	
	@Listener(order=Order.FIRST, beforeModifications = true)
	public void onExplosion(ExplosionEvent.Post event)
	{
		if (event.getTransactions().size() > 100)
		{
            event.setCancelled(true);
		}
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockSnapshot blockSnapshot = transaction.getOriginal();
            if (blockSnapshot.getLocation().isPresent() &&
            		ConfigHandler.getNode("worlds").getNode(transaction.getOriginal().getLocation().get().getExtent().getName()).getNode("enabled").getBoolean() &&
            		!DataHandler.getFlag("explosions", blockSnapshot.getLocation().get()))
    		{
            	transaction.setValid(false);
    		}
		}

	}
}
