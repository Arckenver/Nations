package com.arckenver.nations.service;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.object.Nation;

public class NationsService
{
	public Optional<String> getNationNameOfPlayer(UUID uuid)
	{
		Nation nation = DataHandler.getNationOfPlayer(uuid);
		if (nation == null)
		{
			return Optional.empty();
		}
		return Optional.of(nation.getRealName());
	}
	
	public Optional<String> getNationNameAtLocation(Location<World> loc)
	{
		Nation nation = DataHandler.getNation(loc);
		if (nation == null)
		{
			return Optional.empty();
		}
		return Optional.of(nation.getRealName());
	}
	
	public boolean hasNation(UUID uuid)
	{
		return DataHandler.getNationOfPlayer(uuid) != null;
	}
	
	public boolean isPresident(UUID uuid)
	{
		Nation nation = DataHandler.getNationOfPlayer(uuid);
		if (nation == null)
		{
			return false;
		}
		return nation.isPresident(uuid);
	}
	
	public boolean isMinister(UUID uuid)
	{
		Nation nation = DataHandler.getNationOfPlayer(uuid);
		if (nation == null)
		{
			return false;
		}
		return nation.isMinister(uuid);
	}
}
