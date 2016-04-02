package com.arckenver.nations.listener;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.ExplosionEvent;

import com.arckenver.nations.DataHandler;

public class ExplosionListener
{
	@Listener
	public void onExplosion(ExplosionEvent.Pre event)
	{
		if (!DataHandler.getFlag("explosions", event.getTargetWorld().getLocation(event.getExplosion().getOrigin())))
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
