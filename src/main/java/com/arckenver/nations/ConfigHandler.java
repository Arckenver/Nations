package com.arckenver.nations;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import com.arckenver.nations.object.Nation;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigHandler
{
	private static File configFile;
	private static ConfigurationLoader<CommentedConfigurationNode> configManager;
	private static CommentedConfigurationNode config;

	public static void init(File rootDir)
	{
		configFile = new File(rootDir, "config.conf");
		configManager = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();
	}
	
	public static void load()
	{
		// load file
		try
		{
			if (!configFile.exists())
			{
				configFile.getParentFile().mkdirs();
				configFile.createNewFile();
				config = configManager.load();
				
				setToDefaultConfig(config);
				
				configManager.save(config);
			}
			config = configManager.load();
		}
		catch (IOException e)
		{
			NationsPlugin.getLogger().error("Could not load or create config file !");
			e.printStackTrace();
		}
		
		// check integrity
		for (Entry<Object, ? extends CommentedConfigurationNode> e : config.getNode("worlds").getChildrenMap().entrySet())
		{
			if (!e.getValue().getNode("enabled").getBoolean())
			{
				e.getValue().getNode("enabled").setValue(false);
				e.getValue().removeChild("perms");
				e.getValue().removeChild("flags");
			}
		}
		for (World world : Sponge.getServer().getWorlds())
		{
			CommentedConfigurationNode node = config.getNode("worlds").getNode(world.getName());
			if (node.isVirtual())
			{
				node.getNode("enabled").setValue(true);
				
				node.getNode("perms").getNode(Nation.PERM_BUILD).setValue(true);
				node.getNode("perms").getNode(Nation.PERM_INTERACT).setValue(true);
				
				node.getNode("flags").getNode("pvp").setValue(true);
				node.getNode("flags").getNode("mobs").setValue(true);
				node.getNode("flags").getNode("fire").setValue(true);
				node.getNode("flags").getNode("explosions").setValue(true);
			}
		}
		save();
	}
	
	private static void setToDefaultConfig(CommentedConfigurationNode conf)
	{
		conf.getNode("prices").getNode("nationCreationPrice").setValue(2500);
		conf.getNode("prices").getNode("outpostCreationPrice").setValue(2000);
		conf.getNode("prices").getNode("upkeepPerCitizen").setValue(100);
		conf.getNode("prices").getNode("unclaimRefundPercentage").setValue(0);
		conf.getNode("prices").getNode("extraPrice").setValue(0.5);
		conf.getNode("prices").getNode("blockClaimPrice").setValue(0.3);
		
		conf.getNode("others").getNode("blocksPerCitizen").setValue(1000);
		conf.getNode("others").getNode("blocksPerSpawn").setValue(3500);
		conf.getNode("others").getNode("minNationDistance").setValue(5000);
		conf.getNode("others").getNode("maxExtra").setValue(5000);
		conf.getNode("others").getNode("minNationNameLength").setValue(3);
		conf.getNode("others").getNode("maxNationNameLength").setValue(13);

		conf.getNode("nations").getNode("flags").getNode("pvp").setValue(false);
		conf.getNode("nations").getNode("flags").getNode("mobs").setValue(false);
		conf.getNode("nations").getNode("flags").getNode("fire").setValue(false);
		conf.getNode("nations").getNode("flags").getNode("explosions").setValue(false);
		
		conf.getNode("nations").getNode("perms").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_BUILD).setValue(false);
		conf.getNode("nations").getNode("perms").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_INTERACT).setValue(false);
		conf.getNode("nations").getNode("perms").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_BUILD).setValue(false);
		conf.getNode("nations").getNode("perms").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_INTERACT).setValue(true);
		
		conf.getNode("zones").getNode("perms").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_BUILD).setValue(false);
		conf.getNode("zones").getNode("perms").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_INTERACT).setValue(false);
		conf.getNode("zones").getNode("perms").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_BUILD).setValue(false);
		conf.getNode("zones").getNode("perms").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_INTERACT).setValue(true);
		conf.getNode("zones").getNode("perms").getNode(Nation.TYPE_COOWNER).getNode(Nation.PERM_BUILD).setValue(true);
		conf.getNode("zones").getNode("perms").getNode(Nation.TYPE_COOWNER).getNode(Nation.PERM_INTERACT).setValue(true);
	}

	public static void save()
	{
		try
		{
			configManager.save(config);
		}
		catch (IOException e)
		{
			NationsPlugin.getLogger().error("Could not save config file !");
		}
	}

	public static CommentedConfigurationNode getNode(String path)
	{
		return config.getNode(path);
	}
}
