package com.arckenver.nations.listener;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.ExplosionEvent;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;

public class ExplosionListener
{
	@Listener
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
}
