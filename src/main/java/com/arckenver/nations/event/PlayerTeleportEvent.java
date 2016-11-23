package com.arckenver.nations.event;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlayerTeleportEvent extends AbstractEvent implements TargetPlayerEvent, Cancellable
{
	private final Player player;
	private final Location<World> dest;
	private final Cause cause;
	private boolean cancelled = false;
	
	public PlayerTeleportEvent(Player player, Location<World> dest, Cause cause)
	{
		this.player = player;
		this.dest = dest;
		this.cause = cause;
	}
	
	public Location<World> getDest()
	{
		return dest;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel)
	{
		this.cancelled = cancel;
	}

	@Override
	public Cause getCause()
	{
		return cause;
	}

	@Override
	public Player getTargetEntity()
	{
		return player;
	}

}
