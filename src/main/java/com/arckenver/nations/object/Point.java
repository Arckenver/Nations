package com.arckenver.nations.object;

import org.spongepowered.api.world.World;

public class Point
{
	private World world;
	private int x;
	private int y;
	
	public Point(World world, int x, int y)
	{
		this.world = world;
		this.x = x;
		this.y = y;
	}

	public World getWorld()
	{
		return world;
	}

	public void setWorld(World world)
	{
		this.world = world;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}
}
