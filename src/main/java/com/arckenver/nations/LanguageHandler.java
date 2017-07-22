package com.arckenver.nations;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class LanguageHandler
{
	public static String AA = "get nation details";
	public static String AB = "get details of the nation your standing on";
	public static String AC = "get the list of all nations";
	public static String AD = "create a new nation";
	public static String AE = "deposit money in your nation bank";
	public static String AF = "withdraw money from your nation bank";
	public static String AG = "claim the area you've selected";
	public static String AH = "unclaim the area you've selected";
	public static String AI = "invite player in your nation";
	public static String AJ = "ask nation staff to let you in the nation";
	public static String AK = "kick player out of your nation";
	public static String AL = "leave your nation";
	public static String AM = "resign as the nation president";
	public static String AN = "manage ministers";
	public static String AO = "set nation perm";
	public static String AP = "set nation flag";
	public static String AQ = "teleport to spawn with the given name";
	public static String AR = "set spawn with the given name";
	public static String AS = "delete spawn with the given name";
	public static String AT = "buy extra claimable blocks";
	public static String AU = "get player details";
	public static String AV = "set nation taxes";
	public static String AW = "toggle nation chat";
	public static String AX = "teleport to spawn of a public nation";
	
	public static String BA = "get zone details";
	public static String BB = "get details of the zone your standing on";
	public static String BC = "create a new zone";
	public static String BD = "manage coowners";
	public static String BE = "set zone owner";
	public static String BF = "make this zone owner free";
	public static String BG = "set zone perm";
	public static String BH = "set zone flag";
	public static String BI = "put zone up for sale";
	public static String BJ = "buy the zone your standing on";
	public static String BK = "delete specified zone (or standing on)";

	public static String AZ = "reloads config file";
	public static String BL = "create admin nation";
	public static String AY = "claims for admin nation";
	public static String BM = "delete given nation";
	public static String BN = "set nation's name";
	public static String LP = "set nation's tag";
	public static String BO = "set nation's president";
	public static String BP = "make player join nation";
	public static String BQ = "make player leave nation";
	public static String BR = "manage money";
	public static String BS = "set nation perm";
	public static String BT = "set nation flag";
	
	public static String BU = "get world details";
	public static String BV = "get the list of all worlds";
	public static String BW = "enable nations in specified world";
	public static String BX = "disable nation in specified world";
	public static String BY = "set world perm";
	public static String BZ = "set world flag";

	public static String CA = "You must be an in-game player to perform that command";
	public static String CB = "Invalid nation name";
	public static String CC = "Invalid player name";
	public static String CD = "Invalid argument, you must use \"add\" or \"remove\"";
	public static String CE = "Invalid successor name";
	public static String CF = "Invalid zone name";
	public static String CG = "You must specify nation name";
	public static String CH = "You must specify player name";
	public static String CI = "You must be in a nation to perform that command, type /n ? for more help";
	public static String CJ = "You must be president of your nation to perform that command";
	public static String CK = "You must be president or minister of your nation to perform that command";
	public static String CL = "A new day is here ! Nations will now have to pay their upkeeps";
	public static String CM = "Nation {NATION} could not pay its upkeep and fell into ruins";
	public static String CN = "Nation {NATION} fell into ruins !";
	public static String CO = "There is no nation created yet";
	public static String CP = "You don't have permission to list all zones of that nation";
	public static String CQ = "That player is already president";
	public static String CR = "That player is not part of the nation";
	public static String CS = "Nations plugin is disabled for this world";
	public static String CT = "Invalid world name";
	public static String CU = "You must specify world name";
	public static String CV = "Nations plugin is already enabled for this world";
	public static String CW = "Nations plugin is already disabled for this world";
	public static String CX = "Invalid operation, use \"give\", \"take\" or \"set\"";
	public static String CY = "Could not load or create config file";
	public static String CZ = "Config file has been reloaded";

	public static String DA = "Price must be a positive or null value";
	public static String DB = "Your nation can't buy more than {NUM} extra blocks";
	public static String DC = "There is no economy plugin on this server";
	public static String DD = "Could not get the nation's account on the economy plugin of this server";
	public static String DE = "You need {AMOUNT} to perform that transaction";
	public static String DF = "Your nation needs {AMOUNT} to perform that transaction";
	public static String DG = "Successfully bought {NUM} extra blocks for {AMOUNT}";
	public static String DH = "You don't have that much money";
	public static String DI = "Your nation doesn't have that much money";
	public static String DJ = "Your nation was refunded {AMOUNT} for unclaiming these {NUM} blocks ({PRECENT}%)";
	public static String DK = "You successfully unclaimed this area";
	public static String DL = "You successfully took {AMOUNT} from your nation that has now {BALANCE}";
	public static String DM = "{PLAYER} put zone {ZONE} up for sale at {AMOUNT}";
	public static String DN = "An unexpected error has occured while processing transaction";
	public static String DO = "Could not get your account on the economy plugin of this server";
	public static String DP = "Could not get zone owner's account on the economy plugin of this server";
	public static String DQ = "You must be standing in a nation to perform that command";
	public static String DR = "You do not have permission to buy a zone in this nation";
	public static String DS = "rename zone";
	public static String DT = "You are now speaking in your nation's private channel";
	public static String DU = "You are no longer speaking in your nation's private channel";
	public static String DV = "You are now spying nations' private channels";
	public static String DW = "You are no longer spying nations' private channels";
	public static String DX = "spy on nations' private channels";
	public static String DY = "display nation prices";
	public static String DZ = "force nation upkeep script to run";
	
	public static String EA = "You must select a region with a golden axe first (right/left click)";
	public static String EB = "Your selection must be adjacent to your region";
	public static String EC = "Your selection must intersect your region";
	public static String ED = "Your selection must be included into nation's region";
	public static String EF = "Your selection contains a spawn of your nation";
	public static String EG = "Your nation don't have enough blocks, you can buy extra ones with /n buyextra";
	public static String EH = "You successfully claimed this area";
	public static String EI = "Too close to another nation";
	public static String EJ = "You successfully created an outpost here";
	public static String EK = "You must leave your nation to perform that command";
	public static String EL = "That name is already taken";
	public static String LL = "That tag is already taken";
	public static String EM = "Nation name must be alphanumeric";
	public static String EN = "Nation name must contain at least {MIN} and at most {MAX} characters";
	public static String LM = "Nation tag must be alphanumeric";
	public static String LN = "Nation tag must contain at least {MIN} and at most {MAX} characters";
	public static String EO = "Could not create nation's account, please contact a server administrator";
	public static String EP = "{PLAYER} has created a new nation named {NATION}";
	public static String EQ = "You successfully created nation {NATION}, don't forget to deposit money in the nation's bank with /n deposit";
	public static String ER = "Click to delete spawn {SPAWNLIST} ";
	public static String ES = "Your nation doen't have any spawn with that name";
	public static String ET = "Successfully removed nation spawn";
	public static String EU = "You've successfully given {AMOUNT} to your nation that has now {BALANCE}";
	public static String EV = "You are not standing on any nation's region";
	public static String EW = "That player is already in your nation";
	public static String EX = "Your nation already invited this citizen";
	public static String EY = "{PLAYER} joined the nation";
	public static String EZ = "You joined nation {NATION}";
	public static String FA = "You were invited to join nation {NATION}, {CLICKHERE} to accept invitation";
	public static String FB = "Request was send to {RECEIVER}";
	public static String FC = "You already asked that nation";
	public static String FD = "There are no players in the nation's staff connected yet";
	public static String FE = "{PLAYER} wants to join your nation, {CLICKHERE} to accept demand";
	public static String FF = "That player is not in your nation";
	public static String FG = "You can't kick yourself out of your nation, use /n leave to quit the nation";
	public static String FH = "You can't kick the president out of your nation";
	public static String FI = "You can't kick a fellow minister out of your nation";
	public static String FJ = "{PLAYER} was kicked out of your nation";
	public static String FK = "You were kicked out of your nation by {PLAYER}";
	public static String FL = "You must first resign as president before you leave the nation, use /n resign";
	public static String FM = "You left your nation";
	public static String FN = "{PLAYER} left the nation";
	public static String FO = "You can't add/remove yourself from the ministers of your nation";
	public static String FP = "{PLAYER} is already minister of your nation";
	public static String FQ = "{PLAYER} was successfully added to the ministers of your nation";
	public static String FR = "{PLAYER} added you to the ministers of your nation";
	public static String FS = "{PLAYER} is already not minister of your nation";
	public static String FT = "{PLAYER} was successfully removed from the ministers of your nation";
	public static String FU = "{PLAYER} removed you from the ministers of your nation";
	public static String FV = "{SUCCESSOR} replaces now {PLAYER} as nation's president";
	public static String FW = "Nation {OLDNAME} changed its name to {NEWNAME}";
	public static String LO = "Nation {NAME} changed its tag from {OLDTAG} to {NEWTAG}";
	public static String FX = "Nation spawn must be set inside your territory";
	public static String FY = "Spawn name must be alphanumeric and must contain between {MIN} and {MAX} characters";
	public static String FZ = "Successfully changed the nation spawn";
	public static String GA = "You can teleport to {SPAWNLIST} ";
	public static String GB = "Invalid spawn name, choose between {SPAWNLIST} ";
	public static String GC = "Teleported you to the nation spawn";
	public static String GD = "You must be standing on a zone to perform that command";
	public static String GE = "This zone is not up for sale";
	public static String GF = "You need {AMOUNT} to pay for this zone";
	public static String GG = "You are now the new owner of zone {ZONE}";
	public static String GH = "{PLAYER} bought you zone {ZONE} for {AMOUNT}";
	public static String GI = "You're not standing on any zone of your nation";
	public static String GJ = "You must be owner of that zone to perform that command";
	public static String GK = "You can't add/remove yourself from the coowners of your zone";
	public static String GL = "{PLAYER} is already coowner of your zone";
	public static String GM = "{PLAYER} was successfully added to the coowners of your zone";
	public static String GN = "{PLAYER} added you to the coowners of zone {ZONE}";
	public static String GO = "{PLAYER} is already not coowner of your zone";
	public static String GP = "{PLAYER} was successfully removed from the coowners of your zone";
	public static String GQ = "{PLAYER} removed you from the coowners of zone {ZONE}";
	public static String GR = "There already is a zone with that name in your nation";
	public static String GS = "There is a zone that instersects with your selection";
	public static String GT = "You have successfully created a zone named {ZONE}";
	public static String GU = "You are now the owner of zone {ZONE} inside of your nation";
	public static String GV = "You must own this zone to perform that command";
	public static String GW = "Zone {ZONE} has now no owner";
	public static String GX = "You must be standing on your zone to perform that command";
	public static String GY = "You must own this zone to perform that command";
	public static String GZ = "You must specify zone name or stand on it";
	public static String HA = "{NATION}'s zones are {ZONELIST}";
	public static String HB = "Player is already owner of the zone";
	public static String HC = "New owner must be part of your nation";
	public static String HD = "{PLAYER} is now the new owner of zone {ZONE}";
	public static String HE = "{PLAYER} set you as the owner of zone {ZONE}";
	public static String HF = "Your selection contains a zone of your nation";
	public static String HG = "Selected zone is not inside your nation's region";
	public static String HH = "You don't have permission to build here";
	public static String HI = "You don't have permission to interact here";
	public static String HJ = "Player is not part of a nation";
	public static String HK = "Player is president of his nation, use /na setpres";
	public static String HL = "Success !";
	public static String HM = "You've successfully deleted zone {ZONE} in your nation";
	public static String HN = "Taxes editing is disabled";
	public static String HO = "Taxes can't be higher than {AMOUNT}";
	public static String HP = "You successfully changed your nation's taxes";
	public static String HQ = "You've been kicked out of your nation because you didn't have enough money to pay for the taxes";
	public static String HR = "Your nation can't have more than {MAX} spawns";
	public static String HS = "You renamed the zone to {ZONE}";
	public static String HT = "This nation is not public";
	public static String HU = "Teleport will start in 10 seconds";
	public static String HV = "No spawn named 'home' found. Make one with /n setspawn home";
	public static String HW = "if you have a spawn named 'home', tp to it";
	public static String HX = "Unnamed";
	public static String HY = "manage extra blocks";
	public static String HZ = "manage extra blocks using player name";

	public static String IA = "Wilderness";
	public static String IB = "Nation";
	public static String IC = "Zone";
	public static String ID = "Size";
	public static String IE = "Money";
	public static String IF = "Price";
	public static String IG = "Spawn";
	public static String IH = "President";
	public static String II = "Ministers";
	public static String IJ = "Citizens";
	public static String IK = "Permissions";
	public static String IL = "Outsiders";
	public static String IM = "Flags";
	public static String IN = "Owner";
	public static String IO = "Coowners";
	public static String IP = "None";
	public static String IQ = "Unknown";
	public static String IR = "Not for sale";
	public static String IS = "Citizen";
	public static String LQ = "Minister";
	public static String LR = "Hermit";
	public static String IT = "ENABLED";
	public static String IU = "DISABLED";
	public static String IV = "Taxes";
	public static String IW = "Upkeep";
	public static String IX = "click";
	public static String IY = "Admin";
	public static String IZ = "Zones";
	
	public static String JA = "click here";
	public static String JB = "Nation List";
	public static String JC = "World List";
	public static String JD = "BUILD";
	public static String JE = "INTERACT";
	public static String JG = "true";
	public static String JH = "false";

	public static String KA = "First position set to {COORD}";
	public static String KB = "Second position set to {COORD}";
	
	public static String LA = "Nation prices";
	public static String LB = "Nation creation";
	public static String LC = "Outpost creation";
	public static String LD = "Upkeep per citizen";
	public static String LE = "Price per block claimed";
	public static String LF = "Price per extra block";
	public static String LG = "manage extra spawns";
	public static String LH = "manage extra spawns using player name";
	public static String LI = "unclaims for admin nation";
	public static String LJ = "{PLAYER} made zone {ZONE} not for sale";
	public static String LK = "display nations and zones";
	
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
			if (fields[i].getType() != String.class || language.getNode(fields[i].getName()).getString() == null)
				continue ;
			if (language.getNode(fields[i].getName()) != null) {
				try {
					fields[i].set(String.class, language.getNode(fields[i].getName()).getString());
				} catch (IllegalArgumentException|IllegalAccessException e) {
					NationsPlugin.getLogger().error("Error whey loading language string " + fields[i].getName());
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
