package com.arckenver.nations.object;

import java.util.Date;
import java.util.UUID;

public class Request
{
	private final UUID nationUUID;
	private final UUID playerUUID;
	private final Date date;

	public Request(UUID nationUUID, UUID playerUUID)
	{
		this.nationUUID = nationUUID;
		this.playerUUID = playerUUID;
		this.date = new Date();
	}

	public UUID getNationUUID()
	{
		return nationUUID;
	}

	public UUID getPlayerUUID()
	{
		return playerUUID;
	}

	public Date getDate()
	{
		return date;
	}

	public boolean match(UUID nationUUID, UUID citizenUUID)
	{
		return (this.playerUUID.equals(citizenUUID) && this.nationUUID.equals(nationUUID));
	}
}
