package com.arckenver.nations.object;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.UUID;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.ConfigHandler;
import com.flowpowered.math.vector.Vector2i;
import com.google.common.math.IntMath;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class Nation
{
	public static final String TYPE_OUTSIDER = "outsider";
	public static final String TYPE_CITIZEN = "citizen";
	public static final String TYPE_COOWNER = "coowner";
	
	public static final String PERM_BUILD = "build";
	public static final String PERM_INTERACT = "interact";
	
	private UUID uuid;
	private String name;
	private Hashtable<String, Location<World>> spawns;
	private Region region;
	private UUID president;
	private ArrayList<UUID> ministers;
	private ArrayList<UUID> citizens;
	private Hashtable<String, Hashtable<String, Boolean>> perms;
	private Hashtable<String, Boolean> flags;
	private Hashtable<UUID, Zone> zones;
	private int extras;

	@SuppressWarnings("serial")
	public Nation(UUID uuid, String name)
	{
		this.uuid = uuid;
		this.name = name;
		this.spawns = new Hashtable<String, Location<World>>();
		this.region = new Region();
		this.president = null;
		this.ministers = new ArrayList<UUID>();
		this.citizens = new ArrayList<UUID>();
		this.flags = new Hashtable<String, Boolean>();
		for (Entry<Object, ? extends CommentedConfigurationNode> e : ConfigHandler.getNode("nations").getNode("flags").getChildrenMap().entrySet())
		{
			flags.put(e.getKey().toString(), e.getValue().getBoolean());
		}
		this.perms = new Hashtable<String, Hashtable<String, Boolean>>()
		{{
			put(TYPE_OUTSIDER, new Hashtable<String, Boolean>()
			{{
				put(PERM_BUILD, ConfigHandler.getNode("nations").getNode("perms").getNode(TYPE_OUTSIDER).getNode(PERM_BUILD).getBoolean());
				put(PERM_INTERACT, ConfigHandler.getNode("nations").getNode("perms").getNode(TYPE_OUTSIDER).getNode(PERM_INTERACT).getBoolean());
			}});
			put(TYPE_CITIZEN, new Hashtable<String, Boolean>()
			{{
				put(PERM_BUILD, ConfigHandler.getNode("nations").getNode("perms").getNode(TYPE_CITIZEN).getNode(PERM_BUILD).getBoolean());
				put(PERM_INTERACT, ConfigHandler.getNode("nations").getNode("perms").getNode(TYPE_CITIZEN).getNode(PERM_INTERACT).getBoolean());
			}});
		}};
		this.zones = new Hashtable<UUID, Zone>();
		this.extras = 0;
	}

	public UUID getUUID()
	{
		return uuid;
	}

	public void setUuid(UUID uuid)
	{
		this.uuid = uuid;
	}

	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public Location<World> getSpawn(String name)
	{
		return spawns.get(name);
	}

	public void addSpawn(String name, Location<World> spawn)
	{
		this.spawns.put(name, spawn);
	}
	
	public void removeSpawn(String name)
	{
		this.spawns.remove(name);
	}

	public Hashtable<String, Location<World>> getSpawns()
	{
		return spawns;
	}

	public int getNumSpawns()
	{
		return spawns.size();
	}
	
	public int maxSpawns()
	{
		return 1 + IntMath.divide(region.size(), ConfigHandler.getNode("others").getNode("blocksPerSpawn").getInt(), RoundingMode.FLOOR);
	}

	public Region getRegion()
	{
		return region;
	}

	public void setRegion(Region region)
	{
		this.region = region;
	}

	public UUID getPresident()
	{
		return president;
	}

	public void setPresident(UUID president)
	{
		this.president = president;
	}

	public boolean isPresident(UUID uuid)
	{
		return uuid.equals(president);
	}
	
	public ArrayList<UUID> getMinisters()
	{
		return ministers;
	}
	
	public void addMinister(UUID uuid)
	{
		ministers.add(uuid);
	}

	public void removeMinister(UUID uuid)
	{
		ministers.remove(uuid);
	}
	
	public boolean isMinister(UUID uuid)
	{
		return ministers.contains(uuid);
	}

	public ArrayList<UUID> getStaff()
	{
		ArrayList<UUID> staff = new ArrayList<UUID>();
		staff.add(president);
		staff.addAll(ministers);
		return staff;
	}
	
	public boolean isStaff(UUID uuid)
	{
		return isPresident(uuid) || isMinister(uuid);
	}
	
	public ArrayList<UUID> getCitizens()
	{
		return citizens;
	}
	
	public void addCitizen(UUID uuid)
	{
		citizens.add(uuid);
	}

	public boolean isCitizen(UUID uuid)
	{
		return citizens.contains(uuid);
	}

	public int getNumCitizens()
	{
		return citizens.size();
	}

	public void removeCitizen(UUID uuid)
	{
		zones.values().stream()
				.filter(zone -> uuid.equals(zone.getOwner()))
				.forEach(zone -> zone.setOwner(null));
		ministers.remove(uuid);
		citizens.remove(uuid);
	}
	
	public Hashtable<String, Boolean> getFlags()
	{
		return flags;
	}
	
	public void setFlag(String flag, boolean b)
	{
		flags.put(flag, b);
	}
	
	public boolean getFlag(String flag)
	{
		return flags.get(flag);
	}

	public boolean getFlag(String flag, Location<World> loc)
	{
		Zone zone = getZone(loc);
		if (zone == null || !zone.hasFlag(flag))
		{
			return getFlag(flag);
		}
		return zone.getFlag(flag);
	}

	public boolean getPerm(String type, String perm)
	{
		return perms.get(type).get(perm);
	}

	public Hashtable<String, Hashtable<String, Boolean>> getPerms()
	{
		return perms;
	}

	public void setPerm(String type, String perm, boolean bool)
	{
		perms.get(type).put(perm, bool);
	}

	public Hashtable<UUID, Zone> getZones()
	{
		return zones;
	}
	
	public Zone getZone(Location<World> loc)
	{
		Vector2i p = new Vector2i(loc.getBlockX(), loc.getBlockZ());
		for (Zone zone : zones.values())
		{
			if (zone.getRect().isInside(p))
			{
				return zone;
			}
		}
		return null;
	}

	public void addZone(Zone zone)
	{
		zones.put(zone.getUUID(), zone);
	}
	
	public void removeZone(UUID uuid)
	{
		zones.remove(uuid);
	}

	public int getExtras()
	{
		return extras;
	}

	public void setExtras(int extras)
	{
		this.extras = extras;
	}

	public void addExtras(int extras)
	{
		this.extras += extras;
	}

	public int maxBlockSize()
	{
		return extras + citizens.size() * ConfigHandler.getNode("others").getNode("blocksPerCitizen").getInt();
	}
}
