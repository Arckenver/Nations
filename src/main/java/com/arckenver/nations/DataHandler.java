package com.arckenver.nations;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.channel.NationMessageChannel;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Point;
import com.arckenver.nations.object.Rect;
import com.arckenver.nations.object.Request;
import com.arckenver.nations.object.Zone;
import com.arckenver.nations.serializer.NationDeserializer;
import com.arckenver.nations.serializer.NationSerializer;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.math.IntMath;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DataHandler
{
	private static File nationsDir;
	private static Gson gson;

	private static Hashtable<UUID, Nation> nations;
	private static Hashtable<UUID, Hashtable<Vector2i, ArrayList<Nation>>> worldChunks;
	private static HashMap<UUID, Nation> lastNationWalkedOn;
	private static HashMap<UUID, Zone> lastZoneWalkedOn;
	private static Hashtable<UUID, Point> firstPoints;
	private static Hashtable<UUID, Point> secondPoints;
	private static Hashtable<UUID, UUID> markJobs;
	private static ArrayList<Request> inviteRequests;
	private static ArrayList<Request> joinRequests;
	private static NationMessageChannel spyChannel;

	public static void init(File rootDir)
	{
		nationsDir = new File(rootDir, "nations");
		gson = (new GsonBuilder())
				.registerTypeAdapter(Nation.class, new NationSerializer())
				.registerTypeAdapter(Nation.class, new NationDeserializer())
				.setPrettyPrinting()
				.create();
	}

	public static void load()
	{
		nationsDir.mkdirs();
		nations = new Hashtable<UUID, Nation>();
		for (File f : nationsDir.listFiles())
		{
			if (f.isFile() && f.getName().matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.json"))
			{
				try
				{
					String json = new String(Files.readAllBytes(f.toPath()));
					Nation nation = gson.fromJson(json, Nation.class);
					nations.put(nation.getUUID(), nation);
				}
				catch (IOException e)
				{
					NationsPlugin.getLogger().error("Error while loading file " + f.getName());
					e.printStackTrace();
				}
			}
		}
		calculateWorldChunks();
		lastNationWalkedOn = new HashMap<UUID, Nation>();
		lastZoneWalkedOn = new HashMap<UUID, Zone>();
		firstPoints = new Hashtable<UUID, Point>();
		secondPoints = new Hashtable<UUID, Point>();
		markJobs = new Hashtable<UUID, UUID>();
		inviteRequests = new ArrayList<Request>();
		joinRequests = new ArrayList<Request>();
		spyChannel = new NationMessageChannel();
	}

	public static void save()
	{
		for (UUID uuid : nations.keySet())
		{
			saveNation(uuid);
		}

	}

	public static NationMessageChannel getSpyChannel()
	{
		return spyChannel;
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
			if (nation.getRealName().equalsIgnoreCase(name))
			{
				return nation;
			}
		}
		return null;
	}

	public static Nation getNationByTag(String tag)
	{
		for (Nation nation : nations.values())
		{
			if (nation.getTag().equalsIgnoreCase(tag))
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
		Vector2i area = new Vector2i(IntMath.divide(loc.getBlockX(), 16, RoundingMode.FLOOR), IntMath.divide(loc.getBlockZ(), 16, RoundingMode.FLOOR));
		if (!worldChunks.get(loc.getExtent().getUniqueId()).containsKey(area))
		{
			return null;
		}
		for (Nation nation : worldChunks.get(loc.getExtent().getUniqueId()).get(area))
		{
			if (nation.getRegion().isInside(loc))
			{
				return nation;
			}
		}
		//		for (Entry<Vector2i, ArrayList<Nation>> e : worldChunks.get(loc.getExtent().getUniqueId()).entrySet())
		//		{
		//			if (e.getKey().equals(new Vector2i(IntMath.divide(loc.getBlockX(), 16, RoundingMode.FLOOR), IntMath.divide(loc.getBlockZ(), 16, RoundingMode.FLOOR))))
		//			{
		//				for (Nation nation : e.getValue())
		//				{
		//					if (nation.getRegion().isInside(loc))
		//					{
		//						return nation;
		//					}
		//				}
		//				return null;
		//			}
		//		}
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
		Nation oldNation = getNation(uuid);
		if (oldNation != null) {
			MessageChannel.TO_CONSOLE.send(Text.of("Removing Nation " + uuid + ": "));
			MessageChannel.TO_CONSOLE.send(Utils.formatNationDescription(oldNation, Utils.CLICKER_ADMIN));
		}
		nations.remove(uuid);

		ArrayList<UUID> toRemove = new ArrayList<>();
		for (Nation nation : lastNationWalkedOn.values())
		{
			if (nation != null && nation.getUUID().equals(uuid))
			{
				toRemove.add(nation.getUUID());
			}
		}
		for (UUID uuidToRemove : toRemove)
		{
			lastNationWalkedOn.remove(uuidToRemove);
		}

		calculateWorldChunks();

		inviteRequests.removeIf(req -> req.getNationUUID().equals(uuid));
		joinRequests.removeIf(req -> req.getNationUUID().equals(uuid));

		File file = new File(nationsDir, uuid.toString() + ".json");
		file.delete();
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

		if (nation.isStaff(playerUUID) || zone.isOwner(playerUUID))
			return true;
		if (zone.isCoowner(playerUUID))
			return zone.getPerm(Nation.TYPE_COOWNER, perm);
		if (nation.isCitizen(playerUUID))
			return zone.getPerm(Nation.TYPE_CITIZEN, perm);

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

	public static String getCitizenTitle(UUID uuid)
	{
		if (!ConfigHandler.getNode("others", "enableNationRanks").getBoolean())
		{
			return "";
		}
		Nation nation = getNationOfPlayer(uuid);
		if (nation == null)
		{
			return LanguageHandler.FORMAT_HERMIT;
		}
		if (nation.isPresident(uuid))
		{
			return ConfigHandler.getNationRank(nation.getNumCitizens()).getNode("presidentTitle").getString();
		}
		if (nation.isMinister(uuid))
		{
			return LanguageHandler.FORMAT_MINISTER;
		}
		return LanguageHandler.FORMAT_CITIZEN;
	}

	public static boolean canClaim(Location<World> loc, boolean ignoreMinDistance)
	{
		return canClaim(loc, ignoreMinDistance, null);
	}

	public static boolean canClaim(Location<World> loc, boolean ignoreMinDistance, UUID toExclude)
	{
		for (Nation nation : nations.values())
		{
			if (!nation.getUUID().equals(toExclude) && nation.getRegion().distance(loc) < ConfigHandler.getNode("others", "minNationDistance").getInt())
			{
				if (ignoreMinDistance)
				{
					if (nation.getRegion().isInside(loc))
					{
						return false;
					}
				}
				else
				{
					MessageChannel.TO_CONSOLE.send(Text.of("too close: ", loc, " nation: ", nation.getName()));
					return false;
				}
			}
		}
		return true;
	}

	public static boolean canClaim(Rect rect, boolean ignoreMinDistance, UUID toExclude)
	{
		Optional<World> optWorld = Sponge.getServer().getWorld(rect.getWorld());
		if (!optWorld.isPresent())
		{
			return false;
		}
		World world = optWorld.get();
		return canClaim(world.getLocation(rect.getMaxX(), 0, rect.getMaxY()), ignoreMinDistance, toExclude) &&
				canClaim(world.getLocation(rect.getMaxX(), 0, rect.getMinY()), ignoreMinDistance, toExclude) &&
				canClaim(world.getLocation(rect.getMinX(), 0, rect.getMaxY()), ignoreMinDistance, toExclude) &&
				canClaim(world.getLocation(rect.getMinX(), 0, rect.getMinY()), ignoreMinDistance, toExclude);
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

	// markJobs

	public static void toggleMarkJob(Player player)
	{
		if (markJobs.containsKey(player.getUniqueId()))
		{
			Sponge.getScheduler().getTaskById(markJobs.get(player.getUniqueId())).ifPresent(task -> {task.cancel();});
			markJobs.remove(player.getUniqueId());
			return;
		}
		ParticleEffect nationParticule = ParticleEffect.builder().type(ParticleTypes.DRAGON_BREATH).quantity(1).build();
		ParticleEffect zoneParticule = ParticleEffect.builder().type(ParticleTypes.HAPPY_VILLAGER).quantity(1).build();
		Task t = Sponge.getScheduler()
				.createTaskBuilder()
				.execute(task -> {
					if (!player.isOnline())
					{
						task.cancel();
						markJobs.remove(player.getUniqueId());
						return;
					}
					Location<World> loc = player.getLocation().add(0, 2, 0);
					loc = loc.sub(8, 0, 8);
					for (int x = 0; x < 16; ++x)
					{
						for (int y = 0; y < 16; ++y)
						{
							Nation nation = DataHandler.getNation(loc);
							if (nation != null)
							{
								BlockRay<World> blockRay = BlockRay.from(loc).direction(new Vector3d(0, -1, 0)).distanceLimit(50).stopFilter(BlockRay.blockTypeFilter(BlockTypes.AIR)).build();
								Optional<BlockRayHit<World>> block = blockRay.end();
								if (block.isPresent())
								{
									if (nation.getZone(loc) != null)
									{
										player.spawnParticles(zoneParticule, block.get().getPosition(), 60);
									}
									else
									{
										player.spawnParticles(nationParticule, block.get().getPosition(), 60);
									}
								}
							}
							loc = loc.add(0,0,1);
						}
						loc = loc.add(1,0,0);
						loc = loc.sub(0,0,16);
					}
				})
				.delay(1, TimeUnit.SECONDS)
				.interval(1, TimeUnit.SECONDS)
				.async()
				.submit(NationsPlugin.getInstance());
		markJobs.put(player.getUniqueId(), t.getUniqueId());
	}

	// points

	public static Point getFirstPoint(UUID uuid)
	{
		if (ConfigHandler.getNode("others", "enableGoldenAxe").getBoolean(true))
		{
			return firstPoints.get(uuid);
		}
		Optional<Player> player = Sponge.getServer().getPlayer(uuid);
		if (!player.isPresent())
		{
			return null;
		}
		Vector3i chunk = player.get().getLocation().getChunkPosition();
		return new Point(player.get().getWorld(), chunk.getX() * 16, chunk.getZ() * 16);
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
		if (ConfigHandler.getNode("others", "enableGoldenAxe").getBoolean(true))
		{
			return secondPoints.get(uuid);
		}
		Optional<Player> player = Sponge.getServer().getPlayer(uuid);
		if (!player.isPresent())
		{
			return null;
		}
		Vector3i chunk = player.get().getLocation().getChunkPosition();
		return new Point(player.get().getWorld(), chunk.getX() * 16 + 15, chunk.getZ() * 16 + 15);
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
		Nation nation = nations.get(uuid);
		if (nation == null)
		{
			NationsPlugin.getLogger().warn("Trying to save null nation !");
			return;
		}
		File file = new File(nationsDir, uuid.toString() + ".json");
		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			}
			String json = gson.toJson(nation, Nation.class);
			Files.write(file.toPath(), json.getBytes());
		}
		catch (IOException e)
		{
			NationsPlugin.getLogger().error("Error while saving file " + file.getName() + " for nation " + nation.getName());
		}
	}
}
