package com.arckenver.nations;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;

import com.arckenver.nations.object.Nation;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigHandler
{
	private static Logger logger;
	
	private static File configFile;
	private static ConfigurationLoader<CommentedConfigurationNode> configManager;
	private static CommentedConfigurationNode config;

	public static void init(Logger l, File rootDir)
	{
		logger = l;

		configFile = new File(rootDir, "config.conf");
		configManager = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();
		
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
			logger.error("Could not load or create config file !");
			e.printStackTrace();
		}
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

		conf.getNode("flags").getNode("wilderness").getNode("pvp").setValue(true);
		conf.getNode("flags").getNode("wilderness").getNode("mobs").setValue(true);
		conf.getNode("flags").getNode("wilderness").getNode("fire").setValue(true);
		conf.getNode("flags").getNode("wilderness").getNode("explosions").setValue(true);

		conf.getNode("flags").getNode("nations").getNode("pvp").setValue(false);
		conf.getNode("flags").getNode("nations").getNode("mobs").setValue(false);
		conf.getNode("flags").getNode("nations").getNode("fire").setValue(false);
		conf.getNode("flags").getNode("nations").getNode("explosions").setValue(false);
		
		conf.getNode("perms").getNode("nations").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_BUILD).setValue(false);
		conf.getNode("perms").getNode("nations").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_INTERACT).setValue(false);
		conf.getNode("perms").getNode("nations").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_BUILD).setValue(false);
		conf.getNode("perms").getNode("nations").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_INTERACT).setValue(true);
		
		conf.getNode("perms").getNode("zones").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_BUILD).setValue(false);
		conf.getNode("perms").getNode("zones").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_INTERACT).setValue(false);
		conf.getNode("perms").getNode("zones").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_BUILD).setValue(false);
		conf.getNode("perms").getNode("zones").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_INTERACT).setValue(true);
		conf.getNode("perms").getNode("zones").getNode(Nation.TYPE_COOWNER).getNode(Nation.PERM_BUILD).setValue(true);
		conf.getNode("perms").getNode("zones").getNode(Nation.TYPE_COOWNER).getNode(Nation.PERM_INTERACT).setValue(true);
	}

	public static void save()
	{
		try
		{
			configManager.save(config);
		}
		catch (IOException e)
		{
			logger.error("Could not save config file !");
		}
	}

	public static CommentedConfigurationNode getNode(String path)
	{
		return config.getNode(path);
	}
}
