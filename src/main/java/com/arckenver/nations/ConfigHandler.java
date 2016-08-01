package com.arckenver.nations;

import java.io.File;
import java.io.IOException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
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
		load(null);
	}
	
	public static void load(CommandSource src)
	{
		// load file
		try
		{
			if (!configFile.exists())
			{
				configFile.getParentFile().mkdirs();
				configFile.createNewFile();
				config = configManager.load();
				configManager.save(config);
			}
			config = configManager.load();
		}
		catch (IOException e)
		{
			NationsPlugin.getLogger().error(LanguageHandler.CY);
			e.printStackTrace();
			if (src != null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CY));
			}
		}
		
		// check integrity
		Utils.ensurePositiveNumber(config.getNode("prices").getNode("nationCreationPrice"), 2500);
		Utils.ensurePositiveNumber(config.getNode("prices").getNode("upkeepPerCitizen"), 100);
		Utils.ensurePositiveNumber(config.getNode("prices").getNode("unclaimRefundPercentage"), 0);
		Utils.ensurePositiveNumber(config.getNode("prices").getNode("extraPrice"), 0.5);
		Utils.ensurePositiveNumber(config.getNode("prices").getNode("blockClaimPrice"), 0.3);
		Utils.ensurePositiveNumber(config.getNode("prices").getNode("outpostCreationPrice"), 1000);
		
		Utils.ensurePositiveNumber(config.getNode("others").getNode("blocksPerCitizen"), 1000);
		Utils.ensurePositiveNumber(config.getNode("others").getNode("blocksPerSpawn"), 3500);
		Utils.ensurePositiveNumber(config.getNode("others").getNode("minNationDistance"), 500);
		Utils.ensurePositiveNumber(config.getNode("others").getNode("maxExtra"), 5000);
		Utils.ensurePositiveNumber(config.getNode("others").getNode("minNationNameLength"), 3);
		Utils.ensurePositiveNumber(config.getNode("others").getNode("maxNationNameLength"), 13);
		Utils.ensurePositiveNumber(config.getNode("others").getNode("minZoneNameLength"), 3);
		Utils.ensurePositiveNumber(config.getNode("others").getNode("maxZoneNameLength"), 13);

		Utils.ensureBoolean(config.getNode("nations").getNode("canEditTaxes"), true);
		Utils.ensurePositiveNumber(config.getNode("nations").getNode("defaultTaxes"), 50);
		Utils.ensurePositiveNumber(config.getNode("nations").getNode("maxTaxes"), 100);
		
		Utils.ensureBoolean(config.getNode("nations").getNode("flags").getNode("pvp"), false);
		Utils.ensureBoolean(config.getNode("nations").getNode("flags").getNode("mobs"), false);
		Utils.ensureBoolean(config.getNode("nations").getNode("flags").getNode("fire"), false);
		Utils.ensureBoolean(config.getNode("nations").getNode("flags").getNode("explosions"), false);
		
		Utils.ensureBoolean(config.getNode("nations").getNode("perms").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_BUILD), false);
		Utils.ensureBoolean(config.getNode("nations").getNode("perms").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_INTERACT), false);
		Utils.ensureBoolean(config.getNode("nations").getNode("perms").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_BUILD), false);
		Utils.ensureBoolean(config.getNode("nations").getNode("perms").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_INTERACT), true);
		
		Utils.ensureBoolean(config.getNode("zones").getNode("perms").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_BUILD), false);
		Utils.ensureBoolean(config.getNode("zones").getNode("perms").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_INTERACT), false);
		Utils.ensureBoolean(config.getNode("zones").getNode("perms").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_BUILD), false);
		Utils.ensureBoolean(config.getNode("zones").getNode("perms").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_INTERACT), true);
		Utils.ensureBoolean(config.getNode("zones").getNode("perms").getNode(Nation.TYPE_COOWNER).getNode(Nation.PERM_BUILD), true);
		Utils.ensureBoolean(config.getNode("zones").getNode("perms").getNode(Nation.TYPE_COOWNER).getNode(Nation.PERM_INTERACT), true);
		
		for (World world : Sponge.getServer().getWorlds())
		{
			CommentedConfigurationNode node = config.getNode("worlds").getNode(world.getName());
			
			Utils.ensureBoolean(node.getNode("enabled"), true);
			if (node.getNode("enabled").getBoolean())
			{
				Utils.ensureBoolean(node.getNode("perms").getNode(Nation.PERM_BUILD), true);
				Utils.ensureBoolean(node.getNode("perms").getNode(Nation.PERM_INTERACT), true);
				
				Utils.ensureBoolean(node.getNode("flags").getNode("pvp"), true);
				Utils.ensureBoolean(node.getNode("flags").getNode("mobs"), true);
				Utils.ensureBoolean(node.getNode("flags").getNode("fire"), true);
				Utils.ensureBoolean(node.getNode("flags").getNode("explosions"), true);
			}
			else
			{
				node.removeChild("perms");
				node.removeChild("flags");
			}
		}
		save();
		if (src != null)
		{
			src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.CZ));
		}
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
	
	public static class Utils
	{
		public static void ensureString(CommentedConfigurationNode node, String def)
		{
			if (node.getString() == null)
			{
				node.setValue(def);
			}
		}

		public static void ensurePositiveNumber(CommentedConfigurationNode node, Number def)
		{
			if (!(node.getValue() instanceof Number) || node.getDouble(-1) < 0)
			{
				node.setValue(def);
			}
		}
		
		public static void ensureBoolean(CommentedConfigurationNode node, boolean def)
		{
			if (!(node.getValue() instanceof Boolean))
			{
				node.setValue(def);
			}
		}
	}
}
