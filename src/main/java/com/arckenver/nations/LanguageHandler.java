package com.arckenver.nations;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class LanguageHandler
{
	public static String HELP_DESC_CMD_SETSPAWN = "set spawn with the given name";
	public static String HELP_DESC_CMD_DELSPAWN = "delete spawn with the given name";
	public static String HELP_DESC_CMD_SETNAME = "set nation's name";
	public static String HELP_DESC_CMD_SETTAG = "set nation's tag";
	
	public static String HELP_DESC_CMD_N_INFO = "get nation details";
	public static String HELP_DESC_CMD_N_HERE = "get details of the nation your standing on";
	public static String HELP_DESC_CMD_N_SEE = "display particles in claimed areas";
	public static String HELP_DESC_CMD_N_LIST = "get the list of all nations";
	public static String HELP_DESC_CMD_N_CREATE = "create a new nation";
	public static String HELP_DESC_CMD_N_DEPOSIT = "deposit money in your nation bank";
	public static String HELP_DESC_CMD_N_WITHDRAW = "withdraw money from your nation bank";
	public static String HELP_DESC_CMD_N_CLAIM = "claim the area you've selected";
	public static String HELP_DESC_CMD_N_UNCLAIM = "unclaim the area you've selected";
	public static String HELP_DESC_CMD_N_INVITE = "invite player in your nation";
	public static String HELP_DESC_CMD_N_JOIN = "ask nation staff to let you in the nation";
	public static String HELP_DESC_CMD_N_KICK = "kick player out of your nation";
	public static String HELP_DESC_CMD_N_LEAVE = "leave your nation";
	public static String HELP_DESC_CMD_N_RESIGN = "resign as the nation president";
	public static String HELP_DESC_CMD_N_MINISTER = "manage ministers";
	public static String HELP_DESC_CMD_N_PERM = "set nation perm";
	public static String HELP_DESC_CMD_N_FLAG = "set nation flag";
	public static String HELP_DESC_CMD_N_SPAWN = "teleport to spawn with the given name";
	public static String HELP_DESC_CMD_N_BUYEXTRA = "buy extra claimable blocks";
	public static String HELP_DESC_CMD_N_CITIZEN = "get player details";
	public static String HELP_DESC_CMD_N_TAXES = "set nation taxes";
	public static String HELP_DESC_CMD_N_CHAT = "toggle nation chat";
	public static String HELP_DESC_CMD_N_VISIT = "teleport to spawn of a public nation";
	public static String HELP_DESC_CMD_N_COST = "display nation prices";
	public static String HELP_DESC_CMD_N_HOME = "if you have a spawn named 'home', tp to it";
	
	public static String HELP_DESC_CMD_NA_RELOAD = "reloads config file";
	public static String HELP_DESC_CMD_NA_CREATE = "create admin nation";
	public static String HELP_DESC_CMD_NA_CLAIM = "claims for admin nation";
	public static String HELP_DESC_CMD_NA_DELETE = "delete given nation";
	public static String HELP_DESC_CMD_NA_SETPRES = "set nation's president";
	public static String HELP_DESC_CMD_NA_FORCEJOIN = "make player join nation";
	public static String HELP_DESC_CMD_NA_FORCELEAVE = "make player leave nation";
	public static String HELP_DESC_CMD_NA_ECO = "manage money";
	public static String HELP_DESC_CMD_NA_PERM = "set nation perm";
	public static String HELP_DESC_CMD_NA_FLAG = "set nation flag";
	public static String HELP_DESC_CMD_NA_SPY = "spy on nations' private channels";
	public static String HELP_DESC_CMD_NA_FORCEKEEPUP = "force nation upkeep script to run";
	public static String HELP_DESC_CMD_NA_EXTRA = "manage extra blocks";
	public static String HELP_DESC_CMD_NA_EXTRAPLAYER = "manage extra blocks using player name";
	public static String HELP_DESC_CMD_NA_EXTRASPAWN = "manage extra spawns";
	public static String HELP_DESC_CMD_NA_EXTRASPAWNPLAYER = "manage extra spawns using player name";
	public static String HELP_DESC_CMD_NA_UNCLAIM = "unclaims for admin nation";
	
	public static String HELP_DESC_CMD_Z_INFO = "get zone details";
	public static String HELP_DESC_CMD_Z_LIST = "get details of the zone your standing on";
	public static String HELP_DESC_CMD_Z_CREATE = "create a new zone";
	public static String HELP_DESC_CMD_Z_COOWNER = "manage coowners";
	public static String HELP_DESC_CMD_Z_SETOWNER = "set zone owner";
	public static String HELP_DESC_CMD_Z_DELOWNER = "make this zone owner free";
	public static String HELP_DESC_CMD_Z_PERM = "set zone perm";
	public static String HELP_DESC_CMD_Z_FLAG = "set zone flag";
	public static String HELP_DESC_CMD_Z_SELL = "put zone up for sale";
	public static String HELP_DESC_CMD_Z_BUY = "buy the zone your standing on";
	public static String HELP_DESC_CMD_Z_DELETE = "delete specified zone (or standing on)";
	public static String HELP_DESC_CMD_Z_RENAME = "rename zone";
	
	public static String HELP_DESC_CMD_NW_INFO = "get world details";
	public static String HELP_DESC_CMD_NW_LIST = "get the list of all worlds";
	public static String HELP_DESC_CMD_NW_ENABLE = "enable nations in specified world";
	public static String HELP_DESC_CMD_NW_DISABLE = "disable nation in specified world";
	public static String HELP_DESC_CMD_NW_PERM = "set world perm";
	public static String HELP_DESC_CMD_NW_FLAG = "set world flag";
	
	public static String ERROR_NOPLAYER = "You must be an in-game player to perform that command";
	
	public static String ERROR_BADNATIONNAME = "Invalid nation name";
	public static String ERROR_BADPLAYERNAME = "Invalid player name";
	public static String ERROR_BADWORLDNAME = "Invalid world name";
	public static String ERROR_BADPRESNAME = "Invalid successor name";
	public static String ERROR_BADZONENAME = "Invalid zone name";
	
	public static String ERROR_NEEDNATIONNAME = "You must specify nation name";
	public static String ERROR_NEEDPLAYERNAME = "You must specify player name";
	public static String ERROR_NEEDWORLDNAME = "You must specify world name";
	
	public static String ERROR_NONATION = "You must be in a nation to perform that command, type /n ? for more help";
	public static String ERROR_NONATIONYET = "There is no nation created yet";
	public static String ERROR_PLAYERNOTPARTOFNATION = "That player is not part of the nation";
	public static String ERROR_PLUGINDISABLEDINWORLD = "Nations plugin is disabled for this world";
	public static String ERROR_PLUGINALREADYENABLED = "Nations plugin is already enabled for this world";
	public static String ERROR_PLUGINALREADYDISABLE = "Nations plugin is already disabled for this world";
	
	public static String ERROR_PERM_NATIONPRES = "You must be president of your nation to perform that command";
	public static String ERROR_PERM_NATIONSTAFF = "You must be president or minister of your nation to perform that command";
	public static String ERROR_PERM_LISTZONES = "You don't have permission to list all zones of that nation";
	
	public static String ERROR_PLAYERALREADYPRES = "That player is already president";
	
	public static String ERROR_CONFIGFILE = "Could not load or create config file";
	public static String INFO_CONFIGRELOADED = "Config file has been reloaded";
	
	public static String INFO_UPKEEPANNOUNCE = "A new day is here ! Nations will now have to pay their upkeeps";
	public static String INFO_NATIONFAILUPKEEP = "Nation {NATION} could not pay its upkeep and fell into ruins";
	public static String INFO_NATIONFALL = "Nation {NATION} fell into ruins !";

	public static String ERROR_BADARG_GTS = "Invalid operation, use \"give\", \"take\" or \"set\"";
	public static String ERROR_BADARG_AR = "Invalid argument, you must use \"add\" or \"remove\"";
	public static String ERROR_BADARG_P = "Price must be a positive or null value";
	

	public static String ERROR_NOECO = "There is no economy plugin on this server";
	public static String ERROR_CREATEECONATION = "Could not create nation's account, please contact a server administrator";
	public static String ERROR_ECONONATION = "Could not get the nation's account on the economy plugin of this server";
	public static String ERROR_ECOTRANSACTION = "An unexpected error has occured while processing transaction";
	public static String ERROR_ECONOACCOUNT = "Could not get your account on the economy plugin of this server";
	public static String ERROR_ECONOOWNER = "Could not get zone owner's account on the economy plugin of this server";
	public static String ERROR_NEEDMONEY = "You need {AMOUNT} to perform that transaction";
	public static String ERROR_NEEDMONEYNATION = "Your nation needs {AMOUNT} to perform that transaction";
	public static String ERROR_NOENOUGHMONEY = "You don't have that much money";
	public static String ERROR_NOENOUGHMONEYNATION = "Your nation doesn't have that much money";
	public static String ERROR_NOMOREBLOCK = "Your nation can't buy more than {NUM} extra blocks";
	public static String ERROR_NEEDSTANDNATION = "You must be standing in a nation to perform that command";
	public static String ERROR_PERM_ZONEBUY = "You do not have permission to buy a zone in this nation";
	
	public static String SUCCESS_ADDBLOCKS = "Successfully bought {NUM} extra blocks for {AMOUNT}";
	public static String SUCCESS_UNCLAIM = "You successfully unclaimed this area";
	public static String SUCCESS_WITHDRAW = "You successfully took {AMOUNT} from your nation that has now {BALANCE}";
	
	public static String INFO_UNCLAIMREFUND = "Your nation was refunded {AMOUNT} for unclaiming these {NUM} blocks ({PRECENT}%)";
	public static String INFO_ZONEFORSALE = "{PLAYER} put zone {ZONE} up for sale at {AMOUNT}";
	
	public static String INFO_NATIONCHATON_ON = "You are now speaking in your nation's private channel";
	public static String INFO_NATIONCHAT_OFF = "You are no longer speaking in your nation's private channel";
	public static String INFO_NATIONSPY_ON = "You are now spying nations' private channels";
	public static String INFO_NATIONSPY_OFF = "You are no longer spying nations' private channels";
	
	public static String ERROR_NEEDAXESELECT = "You must select a region with a golden axe first (right/left click)";
	public static String ERROR_NEEDADJACENT = "Your selection must be adjacent to your region";
	public static String ERROR_NEEDINTERSECT = "Your selection must intersect your region";
	public static String ERROR_NEEDINCLUDED = "Your selection must be included into nation's region";
	public static String ERROR_AREACONTAINSPAWN = "Your selection contains a spawn of your nation";
	public static String ERROR_NOENOUGHBLOCKS = "Your nation don't have enough blocks, you can buy extra ones with /n buyextra";
	public static String SUCCESS_CLAIM = "You successfully claimed this area";
	public static String ERROR_TOOCLOSE = "Too close to another nation";
	public static String SUCCESS_OUTPOST = "You successfully created an outpost here";
	public static String ERROR_NEEDLEAVE = "You must leave your nation to perform that command";
	public static String ERROR_NAMETAKEN = "That name is already taken";
	public static String ERROR_TAGTAKEN = "That tag is already taken";
	public static String ERROR_NAMEALPHA = "Nation name must be alphanumeric";
	public static String ERROR_NAMELENGTH = "Nation name must contain at least {MIN} and at most {MAX} characters";
	public static String ERROR_TAGALPHA = "Nation tag must be alphanumeric";
	public static String ERROR_TAGLENGTH = "Nation tag must contain at least {MIN} and at most {MAX} characters";
	public static String INFO_NEWNATIONANNOUNCE = "{PLAYER} has created a new nation named {NATION}";
	public static String INFO_NEWNATION = "You successfully created nation {NATION}, don't forget to deposit money in the nation's bank with /n deposit";
	public static String INFO_CLICK_DELSPAWN = "Click to delete spawn {SPAWNLIST} ";
	public static String ERROR_BADSPAWNNAME = "Your nation doen't have any spawn with that name";
	public static String SUCCESS_DELNATION = "Successfully removed nation spawn";
	public static String SUCCESS_DEPOSIT = "You've successfully given {AMOUNT} to your nation that has now {BALANCE}";
	public static String ERROR_HERE = "You are not standing on any nation's region";
	public static String ERROR_ALREADYINNATION = "That player is already in your nation";
	public static String ERROR_ALREADYINVITED = "Your nation already invited this citizen";
	public static String INFO_JOINNATIONANNOUNCE = "{PLAYER} joined the nation";
	public static String INFO_JOINNATION = "You joined nation {NATION}";
	public static String INFO_CLICK_NATIONINVITE = "You were invited to join nation {NATION}, {CLICKHERE} to accept invitation";
	public static String INFO_INVITSEND = "Request was send to {RECEIVER}";
	public static String ERROR_ALREADYASKED = "You already asked that nation";
	public static String ERROR_NOSTAFFONLINE = "There are no players in the nation's staff connected yet";
	public static String INFO_CLICK_JOINREQUEST = "{PLAYER} wants to join your nation, {CLICKHERE} to accept demand";
	public static String ERROR_NOTINNATION = "That player is not in your nation";
	public static String ERROR_NOKICKSELF = "You can't kick yourself out of your nation, use /n leave to quit the nation";
	public static String ERROR_KICKPRESIDENT = "You can't kick the president out of your nation";
	public static String ERROR_KICKMINISTER = "You can't kick a fellow minister out of your nation";
	public static String SUCCESS_KICK = "{PLAYER} was kicked out of your nation";
	public static String INFO_KICK = "You were kicked out of your nation by {PLAYER}";
	public static String ERROR_NEEDRESIGN = "You must first resign as president before you leave the nation, use /n resign";
	public static String SUCCESS_LEAVENATION = "You left your nation";
	public static String INFO_LEAVENATION = "{PLAYER} left the nation";
	public static String ERROR_PERM_HANDLEMINISTER = "You can't add/remove yourself from the ministers of your nation";
	public static String ERROR_ALREADYMINISTER = "{PLAYER} is already minister of your nation";
	public static String SUCCESS_ADDMINISTER = "{PLAYER} was successfully added to the ministers of your nation";
	public static String INFO_ADDMINISTER = "{PLAYER} added you to the ministers of your nation";
	public static String ERROR_NOMINISTER = "{PLAYER} is already not minister of your nation";
	public static String SUCCESS_DELMINISTER = "{PLAYER} was successfully removed from the ministers of your nation";
	public static String INFO_DELMINISTER = "{PLAYER} removed you from the ministers of your nation";
	public static String INFO_SUCCESSOR = "{SUCCESSOR} replaces now {PLAYER} as nation's president";
	public static String INFO_RENAME = "Nation {OLDNAME} changed its name to {NEWNAME}";
	public static String INFO_TAG = "Nation {NAME} changed its tag from {OLDTAG} to {NEWTAG}";
	public static String ERROR_BADSPAWNLOCATION = "Nation spawn must be set inside your territory";
	public static String ERROR_ALPHASPAWN = "Spawn name must be alphanumeric and must contain between {MIN} and {MAX} characters";
	public static String SUCCESS_CHANGESPAWN = "Successfully changed the nation spawn";
	public static String INFO_TELEPORTLIST = "You can teleport to {SPAWNLIST} ";
	public static String ERROR_SPAWNNAME = "Invalid spawn name, choose between {SPAWNLIST} ";
	public static String INFO_TELEPORTED = "Teleported you to the nation spawn";
	public static String ERROR_NEEDSTANDZONE = "You must be standing on a zone to perform that command";
	public static String ERROR_ZONENFS = "This zone is not up for sale";
	public static String ERROR_ZONENOMONEY = "You need {AMOUNT} to pay for this zone";
	public static String SUCCESS_ZONEBUY = "You are now the new owner of zone {ZONE}";
	public static String INFO_ZONEBUY = "{PLAYER} bought you zone {ZONE} for {AMOUNT}";
	public static String ERROR_NOSTANDZONENATION = "You're not standing on any zone of your nation";
	public static String ERROR_PERM_NOTOWNER = "You must be owner of that zone to perform that command";
	public static String ERROR_PERM_MANAGECOOWNER = "You can't add/remove yourself from the coowners of your zone";
	public static String ERROR_ALREADYCOOWNER = "{PLAYER} is already coowner of your zone";
	public static String SUCCESS_ADDCOOWNER = "{PLAYER} was successfully added to the coowners of your zone";
	public static String INFO_ADDCOOWNER = "{PLAYER} added you to the coowners of zone {ZONE}";
	public static String INFO_ALREADYNOCOOWNER = "{PLAYER} is already not coowner of your zone";
	public static String SUCCESS_DELCOOWNER = "{PLAYER} was successfully removed from the coowners of your zone";
	public static String INFO_DELCOOWNER = "{PLAYER} removed you from the coowners of zone {ZONE}";
	public static String ERROR_ZONENAME = "There already is a zone with that name in your nation";
	public static String ERROR_ZONEINTERSECT = "There is a zone that instersects with your selection";
	public static String SUCCESS_ZONECREATE = "You have successfully created a zone named {ZONE}";
	public static String SUCCESS_SETOWNER = "You are now the owner of zone {ZONE} inside of your nation";
	public static String ERROR_NOOWNER = "You must own this zone to perform that command";
	public static String INFO_NOOWNER = "Zone {ZONE} has now no owner";
	public static String ERROR_NEEDSTANDZONESELF = "You must be standing on your zone to perform that command";
	public static String ERROR_NEEDZONE = "You must specify zone name or stand on it";
	public static String HEADER_ZONELIST = "{NATION}'s zones are {ZONELIST}";
	public static String ERROR_ALREADYOWNER = "Player is already owner of the zone";
	public static String ERROR_OWNERNEEDNATION = "New owner must be part of your nation";
	public static String SUCCESS_CHANGEOWNER = "{PLAYER} is now the new owner of zone {ZONE}";
	public static String INFO_CHANGEOWNER = "{PLAYER} set you as the owner of zone {ZONE}";
	public static String ERROR_SELECTIONCONTAINZONE = "Your selection contains a zone of your nation";
	public static String ERROR_ZONENOTINNATION = "Selected zone is not inside your nation's region";
	public static String ERROR_PERM_BUILD = "You don't have permission to build here";
	public static String ERROR_PERM_INTERACT = "You don't have permission to interact here";
	public static String ERROR_PLAYERNOTINNATION = "Player is not part of a nation";
	public static String ERROR_PLAYERISPRES = "Player is president of his nation, use /na setpres";
	public static String SUCCESS_GENERAL = "Success !";
	public static String SUCCESS_DELZONE = "You've successfully deleted zone {ZONE} in your nation";
	public static String ERROR_TAXEDIT = "Taxes editing is disabled";
	public static String ERROR_TAXMAX = "Taxes can't be higher than {AMOUNT}";
	public static String SUCCESS_CHANGETAX = "You successfully changed your nation's taxes";
	public static String INFO_KICKUPKEEP = "You've been kicked out of your nation because you didn't have enough money to pay for the taxes";
	public static String ERROR_MAXSPAWNREACH = "Your nation can't have more than {MAX} spawns";
	public static String SUCCESS_ZONERENAME = "You renamed the zone to {ZONE}";
	public static String ERROR_NATIONNOTPUBLIC = "This nation is not public";
	public static String INFO_TELEPORTCOOLDOWN = "Teleport will start in 10 seconds";
	public static String ERROR_NOHOME = "No spawn named 'home' found. Make one with /n setspawn home";
	public static String DEFAULT_ZONENAME = "Unnamed";

	public static String TOAST_WILDNAME = "Wilderness";
	public static String TOAST_PVP = "PvP";
	public static String TOAST_NOPVP = "No PvP";
	public static String FORMAT_NATION = "Nation";
	public static String FORMAT_ZONE = "Zone";
	public static String FORMAT_SIZE = "Size";
	public static String FORMAT_MONEY = "Money";
	public static String FORMAT_PRICE = "Price";
	public static String FORMAT_SPAWN = "Spawn";
	public static String FORMAT_PRESIDENT = "President";
	public static String FORMAT_MINISTERS = "Ministers";
	public static String FORMAT_CITIZENS = "Citizens";
	public static String FORMAT_PERMISSIONS = "Permissions";
	public static String FORMAT_OUTSIDERS = "Outsiders";
	public static String FORMAT_FLAGS = "Flags";
	public static String FORMAT_OWNER = "Owner";
	public static String FORMAT_COOWNER = "Coowners";
	public static String FORMAT_NONE = "None";
	public static String FORMAT_UNKNOWN = "Unknown";
	public static String FORMAT_NFS = "Not for sale";
	public static String FORMAT_CITIZEN = "Citizen";
	public static String FORMAT_MINISTER = "Minister";
	public static String FORMAT_HERMIT = "Hermit";
	public static String FLAG_ENABLED = "ENABLED";
	public static String FLAG_DISABLED = "DISABLED";
	public static String FORMAT_TAXES = "Taxes";
	public static String FORMAT_UPKEEP = "Upkeep";
	public static String CLICK = "click";
	public static String FORMAT_ADMIN = "Admin";
	public static String FORMAT_ZONES = "Zones";
	
	public static String CLICKME = "click here";
	public static String HEADER_NATIONLIST = "Nation List";
	public static String HEADER_WORLDLIST = "World List";
	public static String TYPE_BUILD = "BUILD";
	public static String TYPE_INTERACT = "INTERACT";
	public static String VALUE_TRUE = "true";
	public static String VALUE_FALSE = "false";

	public static String AXE_FIRST = "First position set to {COORD}";
	public static String AXE_SECOND = "Second position set to {COORD}";
	
	public static String HEADER_NATIONCOST = "Nation prices";
	public static String COST_MSG_NATIONCREATE = "Nation creation";
	public static String COST_MSG_OUTPOSTCREATE = "Outpost creation";
	public static String COST_MSG_UPKEEP = "Upkeep per citizen";
	public static String COST_MSG_CLAIMPRICE = "Price per block claimed";
	public static String COST_MSG_EXTRAPRICE = "Price per extra block";
	public static String INFO_ZONEFS = "{PLAYER} made zone {ZONE} not for sale";
	
	private static File languageFile;
	private static ConfigurationLoader<CommentedConfigurationNode> languageManager;
	private static CommentedConfigurationNode language;

	public static void init(File rootDir)
	{
		languageFile = new File(rootDir, "language.conf");
		languageManager = HoconConfigurationLoader.builder().setPath(languageFile.toPath()).build();
		
		try
		{
			if (!languageFile.exists())
			{
				languageFile.getParentFile().mkdirs();
				languageFile.createNewFile();
				language = languageManager.load();
				languageManager.save(language);
			}
			language = languageManager.load();
		}
		catch (IOException e)
		{
			NationsPlugin.getLogger().error("Could not load or create language file !");
			e.printStackTrace();
		}
		
	}
	
	public static void load()
	{
		Field fields[] = LanguageHandler.class.getFields();
		for (int i = 0; i < fields.length; ++i) {
			if (fields[i].getType() != String.class)
				continue ;
			if (language.getNode(fields[i].getName()).getString() != null) {
				try {
					fields[i].set(String.class, language.getNode(fields[i].getName()).getString());
				} catch (IllegalArgumentException|IllegalAccessException e) {
					NationsPlugin.getLogger().error("Error whey loading language string " + fields[i].getName());
					e.printStackTrace();
				}
			} else {
				try {
					language.getNode(fields[i].getName()).setValue(fields[i].get(String.class));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					NationsPlugin.getLogger().error("Error whey saving language string " + fields[i].getName());
					e.printStackTrace();
				}
			}
		}
		
		save();
	}

	public static void save()
	{
		try
		{
			languageManager.save(language);
		}
		catch (IOException e)
		{
			NationsPlugin.getLogger().error("Could not save config file !");
		}
	}
}
