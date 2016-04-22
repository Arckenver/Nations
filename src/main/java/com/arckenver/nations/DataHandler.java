package com.arckenver.nations;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Point;
import com.arckenver.nations.object.Rect;
import com.arckenver.nations.object.Region;
import com.arckenver.nations.object.Request;
import com.arckenver.nations.object.Zone;
import com.flowpowered.math.vector.Vector2i;
import com.google.common.math.IntMath;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class DataHandler
{
	private static File dataFile;
	private static ConfigurationLoader<CommentedConfigurationNode> dataManager;
	private static CommentedConfigurationNode data;
	
	private static Hashtable<UUID, Nation> nations;
	private static Hashtable<UUID, Hashtable<Vector2i, ArrayList<Nation>>> worldChunks;
	private static HashMap<UUID, Nation> lastNationWalkedOn;
	private static HashMap<UUID, Zone> lastZoneWalkedOn;
	private static Hashtable<UUID, Point> firstPoints;
	private static Hashtable<UUID, Point> secondPoints;
	private static ArrayList<Request> inviteRequests;
	private static ArrayList<Request> joinRequests;
	
	public static void init(File rootDir)
	{
		dataFile = new File(rootDir, "data.conf");
		dataManager = HoconConfigurationLoader.builder().setPath(dataFile.toPath()).build();
	}
	
	public static void load()
	{
		try
		{
			if (!dataFile.exists())
			{
				dataFile.getParentFile().mkdirs();
				dataFile.createNewFile();
			}
			data = dataManager.load();
			dataManager.save(data);
		}
		catch(IOException e)
		{
			NationsPlugin.getLogger().error("Could not load or create data file !");
			e.printStackTrace();
		}
		
		nations = new Hashtable<UUID, Nation>();
		for (Entry<Object, ? extends CommentedConfigurationNode> e : data.getNode("nations").getChildrenMap().entrySet())
		{
			UUID uuid = UUID.fromString(e.getKey().toString());
			String name = e.getValue().getNode("name").getString();
			Nation nation = new Nation(uuid, name);
			for (Entry<Object, ? extends CommentedConfigurationNode> en : e.getValue().getNode("spawns").getChildrenMap().entrySet())
			{
				nation.addSpawn(en.getKey().toString(), Utils.locFromString(en.getValue().getString()));
			}
			
			Region region = new Region();
			for (Entry<Object, ? extends CommentedConfigurationNode> en : e.getValue().getNode("region").getChildrenMap().entrySet())
			{
				UUID world = UUID.fromString(en.getKey().toString());
				String[] rects = en.getValue().getString().split(Pattern.quote("&"));
				for (int i = 0; i < rects.length; i++)
				{
					Rect r = Utils.rectFromString(rects[i]);
					r.setWorld(world);
					region.addRect(r);
				}
			}
			nation.setRegion(region);
			
			if (!e.getValue().getNode("president").isVirtual())
			{
				nation.setPresident(UUID.fromString(e.getValue().getNode("president").getString()));
			}
			for (CommentedConfigurationNode node : e.getValue().getNode("ministers").getChildrenList())
			{
				nation.addMinister(UUID.fromString(node.getString()));
			}
			for (CommentedConfigurationNode node : e.getValue().getNode("citizens").getChildrenList())
			{
				nation.addCitizen(UUID.fromString(node.getString()));
			}
			for (Entry<Object, ? extends CommentedConfigurationNode> en : e.getValue().getNode("flags").getChildrenMap().entrySet())
			{
				nation.setFlag(en.getKey().toString(), en.getValue().getBoolean());
			}
			for (Entry<Object, ? extends CommentedConfigurationNode> en : e.getValue().getNode("perms").getChildrenMap().entrySet())
			{
				for (Entry<Object, ? extends CommentedConfigurationNode> ent : en.getValue().getChildrenMap().entrySet())
				{
					nation.setPerm(en.getKey().toString(), ent.getKey().toString(), ent.getValue().getBoolean());
				}
			}
			nation.setExtras(e.getValue().getNode("extras").getInt());
			for (Entry<Object, ? extends CommentedConfigurationNode> en : e.getValue().getNode("zones").getChildrenMap().entrySet())
			{
				UUID zoneUUID = UUID.fromString(en.getKey().toString());
				String zoneName = en.getValue().getNode("name").getString();
				Rect rect = Utils.rectFromString(en.getValue().getNode("rect").getNode("points").getString());
				rect.setWorld(UUID.fromString(en.getValue().getNode("rect").getNode("world").getString()));
				Zone zone = new Zone(zoneUUID, zoneName, rect);
				try
				{
					UUID ownerUUID = UUID.fromString(en.getValue().getNode("owner").getString());
					zone.setOwner(ownerUUID);
				}
				catch (IllegalArgumentException ex)
				{
					zone.setOwner(null);
				}
				for (CommentedConfigurationNode node : en.getValue().getNode("coowers").getChildrenList())
				{
					zone.addCoowner(UUID.fromString(node.getString()));
				}
				for (Entry<Object, ? extends CommentedConfigurationNode> ent : en.getValue().getNode("flags").getChildrenMap().entrySet())
				{
					zone.setFlag(ent.getKey().toString(), ent.getValue().getBoolean());
				}
				for (Entry<Object, ? extends CommentedConfigurationNode> ent : en.getValue().getNode("perms").getChildrenMap().entrySet())
				{
					for (Entry<Object, ? extends CommentedConfigurationNode> entr : ent.getValue().getChildrenMap().entrySet())
					{
						zone.setPerm(ent.getKey().toString(), entr.getKey().toString(), entr.getValue().getBoolean());
					}
				}
				if (en.getValue().getNode("price").getValue() instanceof Double)
				{
					zone.setPrice(BigDecimal.valueOf(en.getValue().getNode("price").getDouble()));
				}
				nation.addZone(zone);
			}
			nations.put(nation.getUUID(), nation);
		}
		calculateWorldChunks();
		lastNationWalkedOn = new HashMap<UUID, Nation>();
		lastZoneWalkedOn = new HashMap<UUID, Zone>();
		firstPoints = new Hashtable<UUID, Point>();
		secondPoints = new Hashtable<UUID, Point>();
		inviteRequests = new ArrayList<Request>();
		joinRequests = new ArrayList<Request>();
	}

	public static void save()
	{
		try
		{
			dataManager.save(data);
		}
		catch (IOException e)
		{
			NationsPlugin.getLogger().error("Could not save data file !");
		}
	}
	
	// nations
	
	public static void addNation(Nation nation)
	{
		nations.put(nation.getUUID(), nation);
		saveNation(nation.getUUID());
	}
	
	public static Nation getNation(UUID uuid)
	{
		return nations.get(uuid);
	}

	public static Nation getNation(String name)
	{
		for (Nation nation : nations.values())
		{
			if (nation.getName().equalsIgnoreCase(name))
			{
				return nation;
			}
		}
		return null;
	}

	public static Nation getNation(Location<World> loc)
	{
		if (!worldChunks.containsKey(loc.getExtent().getUniqueId()))
		{
			return null;
		}
		for (Entry<Vector2i, ArrayList<Nation>> e : worldChunks.get(loc.getExtent().getUniqueId()).entrySet())
		{
			if (e.getKey().equals(new Vector2i(IntMath.divide(loc.getBlockX(), 16, RoundingMode.FLOOR), IntMath.divide(loc.getBlockZ(), 16, RoundingMode.FLOOR))))
			{
				for (Nation nation : e.getValue())
				{
					if (nation.getRegion().isInside(loc))
					{
						return nation;
					}
				}
				return null;
			}
		}
		return null;
	}
	
	public static Nation getNationOfPlayer(UUID uuid)
	{
		for (Nation nation : nations.values())
		{
			for (UUID citizen : nation.getCitizens())
			{
				if (citizen.equals(uuid))
				{
					return nation;
				}
			}
		}
		return null;
	}

	public static void removeNation(UUID uuid)
	{
		nations.remove(uuid);
		for (Entry<UUID, Nation> e : lastNationWalkedOn.entrySet())
		{
			if (e.getValue() != null && e.getValue().getUUID().equals(uuid))
			{
				lastNationWalkedOn.remove(e.getKey());
			}
		}
		calculateWorldChunks();
		inviteRequests.removeIf(req -> req.getNationUUID().equals(uuid));
		joinRequests.removeIf(req -> req.getNationUUID().equals(uuid));
		data.getNode("nations").removeChild(uuid.toString());
		save();
	}
	
	public static Hashtable<UUID, Nation> getNations()
	{
		return nations;
	}

	public static boolean getFlag(String flag, Location<World> loc)
	{
		Nation nation = getNation(loc);
		if (nation == null)
		{
			return ConfigHandler.getNode("worlds").getNode(loc.getExtent().getName()).getNode("flags").getNode(flag).getBoolean();
		}
		Zone zone = nation.getZone(loc);
		if (zone == null)
		{
			return nation.getFlag(flag);
		}
		return zone.getFlag(flag);
	}
	
	public static boolean getPerm(String perm, UUID playerUUID, Location<World> loc)
	{
		Nation nation = getNation(loc);
		if (nation == null)
		{
			return ConfigHandler.getNode("worlds").getNode(loc.getExtent().getName()).getNode("perms").getNode(perm).getBoolean();
		}
		Zone zone = nation.getZone(loc);
		if (zone == null)
		{
			if (nation.isCitizen(playerUUID))
			{
				if (nation.isStaff(playerUUID))
				{
					return true;
				}
				return nation.getPerm(Nation.TYPE_CITIZEN, perm);
			}
			return nation.getPerm(Nation.TYPE_OUTSIDER, perm);
		}
		if (nation.isCitizen(playerUUID))
		{
			if (nation.isStaff(playerUUID) || zone.isOwner(playerUUID))
			{
				return true;
			}
			if (zone.isCoowner(playerUUID))
			{
				return zone.getPerm(Nation.TYPE_COOWNER, perm);
			}
			return zone.getPerm(Nation.TYPE_CITIZEN, perm);
		}
		return zone.getPerm(Nation.TYPE_OUTSIDER, perm);
	}
	
	// players
	
	public static String getPlayerName(UUID uuid)
	{
		Optional<Player> optPlayer = Sponge.getServer().getPlayer(uuid);
		if (optPlayer.isPresent())
		{
			return optPlayer.get().getName();
		}
		try
		{
			return Sponge.getServer().getGameProfileManager().get(uuid).get().getName().get();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public static Collection<String> getPlayerNames()
	{
		return Sponge.getServer().getGameProfileManager().getCache().getProfiles().stream().filter(gp -> gp.getName().isPresent()).map(gp -> gp.getName().get()).collect(Collectors.toList());
	}
	
	public static UUID getPlayerUUID(String name)
	{
		Optional<Player> optPlayer = Sponge.getServer().getPlayer(name);
		if (optPlayer.isPresent())
		{
			return optPlayer.get().getUniqueId();
		}
		try
		{
			return Sponge.getServer().getGameProfileManager().get(name).get().getUniqueId();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public static boolean canClaim(Location<World> loc)
	{
		return canClaim(loc, null);
	}
	
	public static boolean canClaim(Location<World> loc, UUID toExclude)
	{
		for (Nation nation : nations.values())
		{
			if (!nation.getUUID().equals(toExclude) && nation.getRegion().distance2(loc) < Math.pow(ConfigHandler.getNode("others").getNode("minNationDistance").getInt(), 2))
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean canClaim(Rect rect, UUID toExclude)
	{
		Optional<World> optWorld = Sponge.getServer().getWorld(rect.getWorld());
		if (!optWorld.isPresent())
		{
			return false;
		}
		World world = optWorld.get();
		return canClaim(world.getLocation(rect.getMaxX(), rect.getMaxY(), 0), toExclude) &&
				canClaim(world.getLocation(rect.getMaxX(), rect.getMinY(), 0), toExclude) &&
				canClaim(world.getLocation(rect.getMinX(), rect.getMaxY(), 0), toExclude) &&
				canClaim(world.getLocation(rect.getMinX(), rect.getMinY(), 0), toExclude);
	}
	
	public static void calculateWorldChunks()
	{
		worldChunks = new Hashtable<UUID, Hashtable<Vector2i, ArrayList<Nation>>>();
		for (Nation nation : nations.values())
		{
			addToWorldChunks(nation);
		}
	}
	
	public static void addToWorldChunks(Nation nation)
	{
		for (Rect r : nation.getRegion().getRects())
		{
			if (!worldChunks.containsKey(r.getWorld()))
			{
				worldChunks.put(r.getWorld(), new Hashtable<Vector2i, ArrayList<Nation>>());
			}
			Hashtable<Vector2i, ArrayList<Nation>> chunks = worldChunks.get(r.getWorld());
			for (int i = IntMath.divide(r.getMinX(), 16, RoundingMode.FLOOR); i < IntMath.divide(r.getMaxX(), 16, RoundingMode.FLOOR) + 1; i++)
			{
				for (int j = IntMath.divide(r.getMinY(), 16, RoundingMode.FLOOR); j < IntMath.divide(r.getMaxY(), 16, RoundingMode.FLOOR) + 1; j++)
				{
					Vector2i vect = new Vector2i(i, j);
					if (!chunks.containsKey(vect))
					{
						chunks.put(vect, new ArrayList<Nation>());
					}
					if (!chunks.get(vect).contains(nation))
					{
						chunks.get(vect).add(nation);
					}
				}
			}
		}
	}
	
	// lastNationWalkedOn
	
	public static Nation getLastNationWalkedOn(UUID uuid)
	{
		return lastNationWalkedOn.get(uuid);
	}

	public static void setLastNationWalkedOn(UUID uuid, Nation nation)
	{
		lastNationWalkedOn.put(uuid, nation);
	}

	public static Zone getLastZoneWalkedOn(UUID uuid)
	{
		return lastZoneWalkedOn.get(uuid);
	}

	public static void setLastZoneWalkedOn(UUID uuid, Zone zone)
	{
		lastZoneWalkedOn.put(uuid, zone);
	}
	
	// points
	
	public static Point getFirstPoint(UUID uuid)
	{
		return firstPoints.get(uuid);
	}
	
	public static void setFirstPoint(UUID uuid, Point point)
	{
		firstPoints.put(uuid, point);
	}
	
	public static void removeFirstPoint(UUID uuid)
	{
		firstPoints.remove(uuid);
	}
	
	public static Point getSecondPoint(UUID uuid)
	{
		return secondPoints.get(uuid);
	}
	
	public static void setSecondPoint(UUID uuid, Point point)
	{
		secondPoints.put(uuid, point);
	}
	
	public static void removeSecondPoint(UUID uuid)
	{
		secondPoints.remove(uuid);
	}

	// requests
	
	public static Request getJoinRequest(UUID nationUUID, UUID uuid)
	{
		for (Request req : joinRequests)
		{
			if (req.match(nationUUID, uuid))
			{
				return req;
			}
		}
		return null;
	}
	
	public static void addJoinRequest(Request req)
	{
		joinRequests.add(req);
	}
	
	public static void removeJoinRequest(Request req)
	{
		joinRequests.remove(req);
	}

	public static Request getInviteRequest(UUID nationUUID, UUID uuid)
	{
		for (Request req : inviteRequests)
		{
			if (req.match(nationUUID, uuid))
			{
				return req;
			}
		}
		return null;
	}
	
	public static void addInviteRequest(Request req)
	{
		inviteRequests.add(req);
	}
	
	public static void removeInviteRequest(Request req)
	{
		inviteRequests.remove(req);
	}
	
	// saves
	
	public static void saveNation(UUID uuid)
	{
		String key = uuid.toString();
		Nation nation = nations.get(uuid);
		if (nation == null)
		{
			NationsPlugin.getLogger().warn("Trying to save null nation !");
			return;
		}
		

		data.getNode("nations").getNode(key).getNode("name").setValue(nation.getName());
		for (Entry<String, Location<World>> e : nation.getSpawns().entrySet())
		{
			data.getNode("nations").getNode(key).getNode("spawns").getNode(e.getKey()).setValue(Utils.locToString(e.getValue()));
		}
		data.getNode("nations").getNode(key).getNode("president").setValue(nation.getPresident().toString());
		data.getNode("nations").getNode(key).removeChild("ministers");
		for (UUID minister : nation.getMinisters())
		{
			data.getNode("nations").getNode(key).getNode("ministers").getAppendedNode().setValue(minister.toString());
		}
		data.getNode("nations").getNode(key).removeChild("citizens");
		for (UUID citizen : nation.getCitizens())
		{
			data.getNode("nations").getNode(key).getNode("citizens").getAppendedNode().setValue(citizen.toString());
		}
		data.getNode("nations").getNode(key).removeChild("flags");
		for (Entry<String, Boolean> e : nation.getFlags().entrySet())
		{
			data.getNode("nations").getNode(key).getNode("flags").getNode(e.getKey()).setValue(e.getValue());
		}
		data.getNode("nations").getNode(key).removeChild("perms");
		for (Entry<String, Hashtable<String, Boolean>> e : nation.getPerms().entrySet())
		{
			for (Entry<String, Boolean> en : e.getValue().entrySet())
			{
				data.getNode("nations").getNode(key).getNode("perms").getNode(e.getKey()).getNode(en.getKey()).setValue(en.getValue());
			}
		}
		Hashtable<UUID, String> rects = new Hashtable<UUID, String>();
		for (Rect r : nation.getRegion().getRects())
		{
			if (rects.containsKey(r.getWorld()))
			{
				String str = rects.get(r.getWorld()).concat("&").concat(Utils.rectToString(r));
				rects.put(r.getWorld(), str);
			}
			else
			{
				rects.put(r.getWorld(), Utils.rectToString(r));
			}
		}
		for (UUID world : rects.keySet())
		{
			data.getNode("nations").getNode(key).getNode("region").getNode(world.toString()).setValue(rects.get(world));
		}
		data.getNode("nations").getNode(key).getNode("extras").setValue(nation.getExtras());
		data.getNode("nations").getNode(key).removeChild("zones");
		for (Zone zone : nation.getZones().values())
		{
			CommentedConfigurationNode node = data.getNode("nations").getNode(key).getNode("zones").getNode(zone.getUUID().toString());
			node.getNode("name").setValue(zone.getName());
			node.getNode("rect").getNode("world").setValue(zone.getRect().getWorld().toString());
			node.getNode("rect").getNode("points").setValue(Utils.rectToString(zone.getRect()));
			node.getNode("owner").setValue((zone.getOwner() == null) ? "null" : zone.getOwner().toString());
			node.removeChild("coowners");
			for (UUID coowner : zone.getCoowners())
			{
				node.getNode("coowners").getAppendedNode().setValue(coowner.toString());
			}
			for (Entry<String, Boolean> e : zone.getFlags().entrySet())
			{
				node.getNode("flags").getNode(e.getKey()).setValue(e.getValue());
			}
			for (Entry<String, Hashtable<String, Boolean>> e : zone.getPerms().entrySet())
			{
				for (Entry<String, Boolean> en : e.getValue().entrySet())
				{
					node.getNode("perms").getNode(e.getKey()).getNode(en.getKey()).setValue(en.getValue());
				}
			}
			if (zone.isForSale())
			{
				node.getNode("price").setValue(zone.getPrice());
			}
		}
		save();
	}
}
