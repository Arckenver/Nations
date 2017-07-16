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
		Utils.ensurePositiveNumber(config.getNode("prices", "nationCreationPrice"), 2500);
		Utils.ensurePositiveNumber(config.getNode("prices", "upkeepPerCitizen"), 100);
		Utils.ensurePositiveNumber(config.getNode("prices", "unclaimRefundPercentage"), 0);
		Utils.ensurePositiveNumber(config.getNode("prices", "extraPrice"), 0.5);
		Utils.ensurePositiveNumber(config.getNode("prices", "blockClaimPrice"), 0.3);
		Utils.ensurePositiveNumber(config.getNode("prices", "outpostCreationPrice"), 1000);
		
		Utils.ensurePositiveNumber(config.getNode("others", "blocksPerCitizen"), 1000);
		Utils.ensurePositiveNumber(config.getNode("others", "maxNationSpawns"), 3);
		Utils.ensurePositiveNumber(config.getNode("others", "minNationDistance"), 500);
		Utils.ensurePositiveNumber(config.getNode("others", "maxExtra"), 5000);
		Utils.ensurePositiveNumber(config.getNode("others", "minNationNameLength"), 3);
		Utils.ensurePositiveNumber(config.getNode("others", "maxNationNameLength"), 13);
		Utils.ensurePositiveNumber(config.getNode("others", "minNationTagLength"), 3);
		Utils.ensurePositiveNumber(config.getNode("others", "maxNationTagLength"), 5);
		Utils.ensurePositiveNumber(config.getNode("others", "minZoneNameLength"), 3);
		Utils.ensurePositiveNumber(config.getNode("others", "maxZoneNameLength"), 13);
		Utils.ensureBoolean(config.getNode("others", "enableNationRanks"), true);
		Utils.ensureBoolean(config.getNode("others", "enableNationTag"), true);
		Utils.ensureString(config.getNode("others", "gravestoneBlock"), "gravestone:gravestone");
		Utils.ensureBoolean(config.getNode("others", "enableGoldenAxe"), true);
		Utils.ensureString(config.getNode("others", "publicChatFormat"), " &r[&3{NATION}&r] {TITLE}");
		Utils.ensureString(config.getNode("others", "nationChatFormat"), " &r{&e{NATION}&r}");
		

		Utils.ensureBoolean(config.getNode("nations", "canEditTaxes"), true);
		Utils.ensurePositiveNumber(config.getNode("nations", "defaultTaxes"), 50);
		Utils.ensurePositiveNumber(config.getNode("nations", "maxTaxes"), 100);
		
		Utils.ensureBoolean(config.getNode("nations", "flags", "pvp"), false);
		Utils.ensureBoolean(config.getNode("nations", "flags", "mobs"), false);
		Utils.ensureBoolean(config.getNode("nations", "flags", "fire"), false);
		Utils.ensureBoolean(config.getNode("nations", "flags", "explosions"), false);
		Utils.ensureBoolean(config.getNode("nations", "flags", "open"), false);
		Utils.ensureBoolean(config.getNode("nations", "flags", "public"), false);
		
		Utils.ensureBoolean(config.getNode("nations", "perms").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_BUILD), false);
		Utils.ensureBoolean(config.getNode("nations", "perms").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_INTERACT), false);
		Utils.ensureBoolean(config.getNode("nations", "perms").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_BUILD), false);
		Utils.ensureBoolean(config.getNode("nations", "perms").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_INTERACT), true);
		
		Utils.ensureBoolean(config.getNode("zones", "perms").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_BUILD), false);
		Utils.ensureBoolean(config.getNode("zones", "perms").getNode(Nation.TYPE_OUTSIDER).getNode(Nation.PERM_INTERACT), false);
		Utils.ensureBoolean(config.getNode("zones", "perms").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_BUILD), false);
		Utils.ensureBoolean(config.getNode("zones", "perms").getNode(Nation.TYPE_CITIZEN).getNode(Nation.PERM_INTERACT), true);
		Utils.ensureBoolean(config.getNode("zones", "perms").getNode(Nation.TYPE_COOWNER).getNode(Nation.PERM_BUILD), true);
		Utils.ensureBoolean(config.getNode("zones", "perms").getNode(Nation.TYPE_COOWNER).getNode(Nation.PERM_INTERACT), true);
		
		if (config.getNode("others", "enableNationRanks").getBoolean())
		{
			if (!config.getNode("nationRanks").hasListChildren() || config.getNode("nationRanks").getChildrenList().isEmpty())
			{
				CommentedConfigurationNode rank;

				rank = config.getNode("nationRanks").getAppendedNode();
				rank.getNode("numCitizens").setValue(1);
				rank.getNode("nationTitle").setValue("Land");
				rank.getNode("presidentTitle").setValue("Leader");

				rank = config.getNode("nationRanks").getAppendedNode();
				rank.getNode("numCitizens").setValue(3);
				rank.getNode("nationTitle").setValue("Federation");
				rank.getNode("presidentTitle").setValue("Count");

				rank = config.getNode("nationRanks").getAppendedNode();
				rank.getNode("numCitizens").setValue(6);
				rank.getNode("nationTitle").setValue("Dominion");
				rank.getNode("presidentTitle").setValue("Duke");

				rank = config.getNode("nationRanks").getAppendedNode();
				rank.getNode("numCitizens").setValue(10);
				rank.getNode("nationTitle").setValue("Kingdom");
				rank.getNode("presidentTitle").setValue("King");

				rank = config.getNode("nationRanks").getAppendedNode();
				rank.getNode("numCitizens").setValue(15);
				rank.getNode("nationTitle").setValue("Empire");
				rank.getNode("presidentTitle").setValue("Emperor");
			}
			boolean defaultRankMissing = true;
			for (CommentedConfigurationNode rank : config.getNode("nationRanks").getChildrenList())
			{
				Utils.ensurePositiveNumber(rank.getNode("numCitizens"), 1000000);
				Utils.ensureString(rank.getNode("nationTitle"), "NO_TITLE");
				Utils.ensureString(rank.getNode("presidentTitle"), "NO_TITLE");
				if (rank.getNode("numCitizens").getInt() == 0)
					defaultRankMissing = false;
			}
			if (defaultRankMissing) {
				CommentedConfigurationNode rank = config.getNode("nationRanks").getAppendedNode();
				rank.getNode("numCitizens").setValue(0);
				rank.getNode("nationTitle").setValue("Virtual");
				rank.getNode("presidentTitle").setValue("Leader");
			}
		}
		
		for (World world : Sponge.getServer().getWorlds())
		{
			CommentedConfigurationNode node = config.getNode("worlds").getNode(world.getName());
			
			Utils.ensureBoolean(node.getNode("enabled"), true);
			if (node.getNode("enabled").getBoolean())
			{
				Utils.ensureBoolean(node.getNode("perms").getNode(Nation.PERM_BUILD), true);
				Utils.ensureBoolean(node.getNode("perms").getNode(Nation.PERM_INTERACT), true);
				
				Utils.ensureBoolean(node.getNode("flags", "pvp"), true);
				Utils.ensureBoolean(node.getNode("flags", "mobs"), true);
				Utils.ensureBoolean(node.getNode("flags", "fire"), true);
				Utils.ensureBoolean(node.getNode("flags", "explosions"), true);
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

	public static CommentedConfigurationNode getNode(String... path)
	{
		return config.getNode((Object[]) path);
	}
	
	public static CommentedConfigurationNode getNationRank(int numCitizens)
	{
		CommentedConfigurationNode rank = config.getNode("nationRanks")
				.getChildrenList()
				.stream()
				.filter(node -> node.getNode("numCitizens").getInt() <= numCitizens)
				.max((CommentedConfigurationNode a, CommentedConfigurationNode b) ->
						Integer.compare(a.getNode("numCitizens").getInt(), b.getNode("numCitizens").getInt()))
				.get();
		return rank;
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
