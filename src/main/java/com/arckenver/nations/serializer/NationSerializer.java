package com.arckenver.nations.serializer;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.UUID;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Rect;
import com.arckenver.nations.object.Zone;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class NationSerializer implements JsonSerializer<Nation>
{
	@Override
	public JsonElement serialize(Nation nation, Type type, JsonSerializationContext ctx)
	{
		JsonObject json = new JsonObject();
		
		json.add("uuid", new JsonPrimitive(nation.getUUID().toString()));
		json.add("name", new JsonPrimitive(nation.getRealName()));
		json.add("admin", new JsonPrimitive(nation.isAdmin()));
		
		if (nation.hasTag())
			json.add("tag", new JsonPrimitive(nation.getTag()));
		
		JsonObject flags = new JsonObject();
		for (Entry<String, Boolean> e : nation.getFlags().entrySet())
		{
			flags.add(e.getKey(), new JsonPrimitive(e.getValue()));
		}
		json.add("flags", flags);
		
		JsonObject perms = new JsonObject();
		for (Entry<String, Hashtable<String, Boolean>> e : nation.getPerms().entrySet())
		{
			JsonObject obj = new JsonObject();
			for (Entry<String, Boolean> en : e.getValue().entrySet())
			{
				obj.add(en.getKey(), new JsonPrimitive(en.getValue()));
			}
			perms.add(e.getKey(), obj);
		}
		json.add("perms", perms);
		
		JsonArray rectArray = new JsonArray();
		for (Rect r : nation.getRegion().getRects())
		{
			JsonObject rectJson = new JsonObject();
			rectJson.add("world", new JsonPrimitive(r.getWorld().toString()));
			rectJson.add("minX", new JsonPrimitive(r.getMinX()));
			rectJson.add("maxX", new JsonPrimitive(r.getMaxX()));
			rectJson.add("minY", new JsonPrimitive(r.getMinY()));
			rectJson.add("maxY", new JsonPrimitive(r.getMaxY()));
			rectArray.add(rectJson);
		}
		json.add("rects", rectArray);
		
		JsonArray zonesArray = new JsonArray();
		for (Zone zone : nation.getZones().values())
		{
			JsonObject zoneObj = new JsonObject();
			
			zoneObj.add("uuid", new JsonPrimitive(zone.getUUID().toString()));
			if (zone.isNamed())
				zoneObj.add("name", new JsonPrimitive(zone.getRealName()));
			
			JsonObject rectJson = new JsonObject();
			rectJson.add("world", new JsonPrimitive(zone.getRect().getWorld().toString()));
			rectJson.add("minX", new JsonPrimitive(zone.getRect().getMinX()));
			rectJson.add("maxX", new JsonPrimitive(zone.getRect().getMaxX()));
			rectJson.add("minY", new JsonPrimitive(zone.getRect().getMinY()));
			rectJson.add("maxY", new JsonPrimitive(zone.getRect().getMaxY()));
			zoneObj.add("rect", rectJson);
			
			if (zone.getOwner() != null)
			{
				zoneObj.add("owner", new JsonPrimitive(zone.getOwner().toString()));
			}
			
			JsonArray coownersArray = new JsonArray();
			for (UUID coowner : zone.getCoowners())
			{
				coownersArray.add(new JsonPrimitive(coowner.toString()));
			}
			zoneObj.add("coowners", coownersArray);
			
			JsonObject zoneFlags = new JsonObject();
			for (Entry<String, Boolean> e : zone.getFlags().entrySet())
			{
				zoneFlags.add(e.getKey(), new JsonPrimitive(e.getValue()));
			}
			zoneObj.add("flags", zoneFlags);
			
			JsonObject zonePerms = new JsonObject();
			for (Entry<String, Hashtable<String, Boolean>> e : zone.getPerms().entrySet())
			{
				JsonObject obj = new JsonObject();
				for (Entry<String, Boolean> en : e.getValue().entrySet())
				{
					obj.add(en.getKey(), new JsonPrimitive(en.getValue()));
				}
				zonePerms.add(e.getKey(), obj);
			}
			zoneObj.add("perms", zonePerms);
			
			if (zone.isForSale())
			{
				zoneObj.add("price", new JsonPrimitive(zone.getPrice()));
			}
			
			zonesArray.add(zoneObj);
		}
		json.add("zones", zonesArray);
		
		if (!nation.isAdmin())
		{
			json.add("president", new JsonPrimitive(nation.getPresident().toString()));
			
			JsonArray ministersArray = new JsonArray();
			for (UUID minister : nation.getMinisters())
			{
				ministersArray.add(new JsonPrimitive(minister.toString()));
			}
			json.add("ministers", ministersArray);
			
			JsonArray citizensArray = new JsonArray();
			for (UUID citizen : nation.getCitizens())
			{
				citizensArray.add(new JsonPrimitive(citizen.toString()));
			}
			json.add("citizens", citizensArray);

			json.add("taxes", new JsonPrimitive(nation.getTaxes()));
			json.add("extras", new JsonPrimitive(nation.getExtras()));
			json.add("extraspawns", new JsonPrimitive(nation.getExtraSpawns()));
			
			JsonObject spawns = new JsonObject();
			for (Entry<String, Location<World>> e : nation.getSpawns().entrySet())
			{
				JsonObject loc = new JsonObject();
				loc.add("world", new JsonPrimitive(e.getValue().getExtent().getUniqueId().toString()));
				loc.add("x", new JsonPrimitive(e.getValue().getX()));
				loc.add("y", new JsonPrimitive(e.getValue().getY()));
				loc.add("z", new JsonPrimitive(e.getValue().getZ()));
				
				spawns.add(e.getKey(), loc);
			}
			json.add("spawns", spawns);
			
		}
		return json;
	}
}
