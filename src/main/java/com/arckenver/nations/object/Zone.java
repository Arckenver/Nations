package com.arckenver.nations.object;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.UUID;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.LanguageHandler;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class Zone
{
	private UUID uuid;
	private String name;
	private UUID owner;
	private ArrayList<UUID> coowners;
	private Rect rect;
	private Hashtable<String, Hashtable<String, Boolean>> perms;
	private Hashtable<String, Boolean> flags;
	private BigDecimal price;
	
	public Zone(UUID uuid, String name, Rect rect)
	{
		this(uuid, name, rect, null);
	}
	
	@SuppressWarnings("serial")
	public Zone(UUID uuid, String name, Rect rect, UUID owner)
	{
		this.uuid = uuid;
		this.name = name;
		this.owner = owner;
		this.coowners = new ArrayList<UUID>();
		this.rect = rect;
		

		this.flags = new Hashtable<String, Boolean>();
		for (Entry<Object, ? extends CommentedConfigurationNode> e : ConfigHandler.getNode("flags", "zones").getChildrenMap().entrySet())
		{
			flags.put(e.getKey().toString(), e.getValue().getBoolean());
		}
		this.perms = new Hashtable<String, Hashtable<String, Boolean>>()
		{{
			put(Nation.TYPE_OUTSIDER, new Hashtable<String, Boolean>()
			{{
				put(Nation.PERM_BUILD, ConfigHandler.getNode("zones", "perms").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_BUILD).getBoolean());
				put(Nation.PERM_INTERACT, ConfigHandler.getNode("zones", "perms").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_INTERACT).getBoolean());
			}});
			put(Nation.TYPE_CITIZEN, new Hashtable<String, Boolean>()
			{{
				put(Nation.PERM_BUILD, ConfigHandler.getNode("zones", "perms").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_BUILD).getBoolean());
				put(Nation.PERM_INTERACT, ConfigHandler.getNode("zones", "perms").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_INTERACT).getBoolean());
			}});
			put(Nation.TYPE_COOWNER, new Hashtable<String, Boolean>()
			{{
				put(Nation.PERM_BUILD, ConfigHandler.getNode("zones", "perms").getNode(Nation.TYPE_COOWNER).getNode(Nation.PERM_BUILD).getBoolean());
				put(Nation.PERM_INTERACT, ConfigHandler.getNode("zones", "perms").getNode(Nation.TYPE_COOWNER).getNode(Nation.PERM_INTERACT).getBoolean());
			}});
		}};
	}
	
	public UUID getUUID()
	{
		return uuid;
	}

	public String getName()
	{
      if (name == null)
			return LanguageHandler.HX;
		return name.replace("_", " ");
	}
	
	public String getRealName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public UUID getOwner()
	{
		return owner;
	}

	public void setOwner(UUID owner)
	{
		this.owner = owner;
	}

	public boolean isOwner(UUID uuid)
	{
		return owner != null && owner.equals(uuid);
	}
	
	public boolean isOwned()
	{
		return owner != null;
	}
	
	public boolean isNamed()
	{
		return name != null;
	}

	public ArrayList<UUID> getCoowners()
	{
		return coowners;
	}

	public void addCoowner(UUID coowner)
	{
		this.coowners.add(coowner);
	}

	public void removeCoowner(UUID coowner)
	{
		this.coowners.remove(coowner);
	}

	public void resetCoowners()
	{
		this.coowners = new ArrayList<UUID>();
	}

	public boolean isCoowner(UUID uuid)
	{
		return coowners.contains(uuid);
	}
	
	public Rect getRect()
	{
		return rect;
	}

	public void setRect(Rect rect)
	{
		this.rect = rect;
	}

	public boolean getFlag(String flag)
	{
		return flags.get(flag);
	}

	public Hashtable<String, Boolean> getFlags()
	{
		return flags;
	}

	public void setFlag(String flag, boolean b)
	{
		flags.put(flag, b);
	}

	public boolean hasFlag(String flag)
	{
		return flags.containsKey(flag);
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
	
	public BigDecimal getPrice()
	{
		return price;
	}
	
	public void setPrice(BigDecimal price)
	{
		this.price = price;
	}
	
	public boolean isForSale()
	{
		return price != null;
	}
}
