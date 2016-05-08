package com.arckenver.nations.object;

import java.util.ArrayList;
import java.util.UUID;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector2i;

public class Rect
{
	private UUID world;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	
	public Rect(UUID world, Vector2i point)
	{
		this(world, point.getX(), point.getX(), point.getY(), point.getY());
	}

	public Rect(Point a, Point b)
	{
		this(a.getWorld().getUniqueId(), Math.min(a.getX(), b.getX()), Math.max(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.max(a.getY(), b.getY()));
	}

	public Rect(UUID world, int minX, int maxX, int minY, int maxY)
	{
		this.world = world;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	public UUID getWorld()
	{
		return world;
	}

	public void setWorld(UUID world)
	{
		this.world = world;
	}

	public int getMinX()
	{
		return minX;
	}

	public void setMinX(int minX)
	{
		this.minX = minX;
	}

	public int getMaxX()
	{
		return maxX;
	}

	public void setMaxX(int maxX)
	{
		this.maxX = maxX;
	}

	public int getMinY()
	{
		return minY;
	}

	public void setMinY(int minY)
	{
		this.minY = minY;
	}

	public int getMaxY()
	{
		return maxY;
	}

	public void setMaxY(int maxY)
	{
		this.maxY = maxY;
	}

	public int width()
	{
		return maxX - minX + 1;
	}
	
	public int height()
	{
		return maxY - minY + 1;
	}
	
	public int size()
	{
		return width()*height();
	}
	
	public boolean isInside(Vector2i point)
	{
		return minX <= point.getX() && point.getX() <= maxX && minY <= point.getY() && point.getY() <= maxY;
	}

	public boolean isInside(Location<World> loc)
	{
		return loc.getExtent().getUniqueId().equals(world) && isInside(new Vector2i(loc.getBlockX(), loc.getBlockZ()));
	}
	
	public boolean intersects(Rect rect)
	{
		return this.minX <= rect.maxX && rect.minX <= this.maxX && this.minY <= rect.maxY && rect.minY <= this.maxY;
	}

	public ArrayList<Vector2i> pointsInside(Rect rect)
	{
		ArrayList<Vector2i> points = new ArrayList<Vector2i>();
		Vector2i A = new Vector2i(minX, minY);
		if (rect.isInside(A))
			points.add(A);
		Vector2i B = new Vector2i(minX, maxY);
		if (rect.isInside(B))
			points.add(B);
		Vector2i C = new Vector2i(maxX, minY);
		if (rect.isInside(C))
			points.add(C);
		Vector2i D = new Vector2i(maxX, maxY);
		if (rect.isInside(D))
			points.add(D);
		return points;
	}

	public int distance2(Vector2i point)
	{
		if (isInside(point))
		{
			return 0;
		}
		return Math.min(
				Math.min(
						point.distanceSquared(new Vector2i(minX, minY)),
						point.distanceSquared(new Vector2i(minX, maxY))),
				Math.min(
						point.distanceSquared(new Vector2i(maxX, minY)),
						point.distanceSquared(new Vector2i(maxX, maxY))));
	}

	public boolean isAdjacent(Rect rect)
	{
		return (new Rect(world, minX - 1, maxX + 1, minY - 1, maxY + 1)).intersects(rect);
	}

	public boolean isAdjacent(Vector2i point)
	{
		return this.minX - 1 <= point.getX() && point.getX() <= this.maxX + 1 && this.minY - 1 <= point.getY() && point.getY() <= this.maxY + 1;
	}
	
	public ArrayList<Rect> cutBy(Rect rect)
	{
		ArrayList<Rect> result = new ArrayList<Rect>();
		ArrayList<Vector2i> points = this.pointsInside(rect);
		if (points.size() == 4)
		{
			return result;
		}
		else if (points.size() == 2)
		{
			Rect r = new Rect(world, minX, maxX, minY, maxY);
			if (points.get(0).getX() == points.get(1).getX())
			{
				if (points.get(0).getX() <= rect.getMaxX())
				{
					r.setMinX(rect.getMaxX() + 1);
				}
				else
				{
					r.setMaxX(rect.getMinX() - 1);
				}
			}
			else
			{
				if (points.get(0).getY() <= rect.getMaxY())
				{
					r.setMinY(rect.getMaxY() + 1);
				}
				else
				{
					r.setMaxY(rect.getMinY() - 1);
				}
			}
			result.add(r);
		}
		else if (points.size() == 1)
		{
			if (points.get(0).getX() == maxX && points.get(0).getY() == maxY)
			{
				result.add(new Rect(world, minX, rect.getMinX() - 1, rect.getMinY(), maxY));
				result.add(new Rect(world, minX, maxX, minY, rect.getMinY() - 1));
			}
			else if (points.get(0).getX() == minX && points.get(0).getY() == maxY)
			{
				result.add(new Rect(world, minX, rect.getMaxX(), minY, rect.getMinY() - 1));
				result.add(new Rect(world, rect.getMaxX() + 1, maxX, minY, maxY));
			}
			else if (points.get(0).getX() == minX && points.get(0).getY() == minY)
			{
				result.add(new Rect(world, rect.getMaxX() + 1, maxX, minY, rect.getMaxY()));
				result.add(new Rect(world, minX, maxX, rect.getMaxY() + 1, maxY));
			}
			else if (points.get(0).getX() == maxX && points.get(0).getY() == minY)
			{
				result.add(new Rect(world, rect.getMinX(), maxX, rect.getMaxY() + 1, maxY));
				result.add(new Rect(world, minX, rect.getMinX() - 1, minY, maxY));
			}
		}
		else if (points.size() == 0)
		{
			if (!intersects(rect))
			{
				result.add(new Rect(world, minX, maxX, minY, maxY));
			}
			else if (rect.getMinX() > minX && rect.getMaxX() < maxX && (!(rect.getMaxY() >= minY || maxY >= rect.getMinY())))
			{
				result.add(new Rect(world, minX, rect.getMinX() - 1, minY, maxY));
				result.add(new Rect(world, rect.getMaxX() + 1, maxX, minY, maxY));
				if (rect.getMinY() <= minY)
				{
					if (rect.getMaxY() < maxY)
					{
						result.add(new Rect(world, rect.getMinX(), rect.getMaxX(), rect.getMaxY() + 1, maxY));
					}
				}
				else
				{
					result.add(new Rect(world, rect.getMinX(), rect.getMaxX(), minY, rect.getMinY() - 1));
					if (rect.getMaxY() < maxY)
					{
						result.add(new Rect(world, rect.getMinX(), rect.getMaxX(), rect.getMaxY() + 1, maxY));
					}
				}
			}
			else if (rect.getMinY() > minY && rect.getMaxY() < maxY)
			{
				result.add(new Rect(world, minX, maxX, minY, rect.getMinY() - 1));
				result.add(new Rect(world, minX, maxX, rect.getMaxY() + 1, maxY));
				if (rect.getMinX() <= minX)
				{
					if (rect.getMaxX() < maxX)
					{
						result.add(new Rect(world, rect.getMaxX() + 1, maxX, rect.getMinY(), rect.getMaxY()));
					}
				}
				else
				{
					result.add(new Rect(world, minX, rect.getMinX() - 1, rect.getMinY(), rect.getMaxY()));
					if (rect.getMaxY() < maxY)
					{
						result.add(new Rect(world, rect.getMaxX() + 1, maxX, rect.getMinY(), rect.getMaxY()));
					}
				}
			}
		}
		return result;
	}
}
