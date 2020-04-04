package com.arckenver.nations.object;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.UUID;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector2i;

public class Region
{
	private ArrayList<Rect> rects;
	private Hashtable<UUID, Rect> extrema;
	
	public Region()
	{
		this(new ArrayList<Rect>());
	}
	
	public Region(ArrayList<Rect> rects)
	{
		this.rects = rects;
		this.extrema = new Hashtable<UUID, Rect>();
		calculateExtr();
	}
	
	private void calculateExtr()
	{
		for (Rect r : rects)
		{
			if (!extrema.containsKey(r.getWorld()))
			{
				extrema.put(r.getWorld(), new Rect(r.getWorld(), Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE));
			}
			Rect rect = extrema.get(r.getWorld());
			if (r.getMaxX() > rect.getMaxX()) rect.setMaxX(r.getMaxX());
			if (r.getMinX() < rect.getMinX()) rect.setMinX(r.getMinX());
			if (r.getMaxY() > rect.getMaxY()) rect.setMaxY(r.getMaxY());
			if (r.getMinY() < rect.getMinY()) rect.setMinY(r.getMinY());
		}
	}
	
	public ArrayList<Rect> getRects()
	{
		return rects;
	}

	public void addRect(Rect rect)
	{
		ArrayList<Rect> toAdd = new ArrayList<Rect>();
		toAdd.add(rect);
		for (Iterator<Rect> it = rects.iterator(); it.hasNext();)
		{
			Rect r = it.next();
			ArrayList<Rect> futureToAdd = new ArrayList<Rect>();
			for (Rect rectToAdd : toAdd)
			{
				futureToAdd.addAll(rectToAdd.cutBy(r));
			}
			if (r.useless())
				it.remove();
			toAdd = futureToAdd;
		}
		rects.addAll(toAdd);
		calculateExtr();
	}

	public void removeRect(Rect rect)
	{
		ArrayList<Rect> futureRects = new ArrayList<Rect>();
		for (Rect r : rects)
		{
			futureRects.addAll(r.cutBy(rect));
		}
		rects = futureRects;
		calculateExtr();
	}

	public int size()
	{
		int s = 0;
		for (Rect r : rects)
		{
			s += r.size();
		}
		return s;
	}

	public boolean isInside(Location<World> loc)
	{
		if(loc == null) return false;
		Rect extr = extrema.get(loc.getExtent().getUniqueId());
		if(extr == null) return false;
		Vector2i p = new Vector2i(loc.getBlockX(), loc.getBlockZ());
		if (p.getX() < extr.getMinX() || p.getX() > extr.getMaxX() || p.getY() < extr.getMinY() || p.getY() > extr.getMaxY())
		{
			return false;
		}
		for (Rect r : rects)
		{
			if (r.isInside(p))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isInside(Rect rect)
	{
		Rect extr = extrema.get(rect.getWorld());
		if (rect.getMaxX() < extr.getMinX() || rect.getMinX() > extr.getMaxX() || rect.getMinY() < extr.getMinX() || rect.getMinY() > extr.getMaxY())
		{
			return false;
		}
		ArrayList<Rect> cut = new ArrayList<Rect>();
		cut.add(rect);
		for (Rect r : rects)
		{
			ArrayList<Rect> newCut = new ArrayList<Rect>();
			for (Rect re : cut)
			{
				newCut.addAll(re.cutBy(r));
			}
			cut = newCut;
		}
		return cut.isEmpty();
	}
	
	public boolean isAdjacent(Vector2i point)
	{
		for (Rect r : rects)
		{
			if (r.isAdjacent(point))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isAdjacent(Rect rect)
	{
		for (Rect r : rects)
		{
			if (r.isAdjacent(rect))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean intersects(Rect rect)
	{
		for (Rect r : rects)
		{
			if (r.intersects(rect))
			{
				return true;
			}
		}
		return false;
	}

	public float distance(Location<World> loc)
	{
		float dist = Float.MAX_VALUE;
		for (Rect r : rects)
		{
			if (loc.getExtent().getUniqueId().equals(r.getWorld()))
			{
				dist = Math.min(dist, r.distance(new Vector2i(loc.getBlockX(), loc.getBlockZ())));
			}
		}
		return dist;
	}

	public Region copy()
	{
		ArrayList<Rect> rectsCopy = new ArrayList<Rect>();
		for (Rect r : rects)
		{
			rectsCopy.add(new Rect(r.getWorld(), r.getMinX(), r.getMaxX(), r.getMinY(), r.getMaxY()));
		}
		return new Region(rectsCopy);
	}
}
