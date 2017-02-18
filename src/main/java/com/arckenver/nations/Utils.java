package com.arckenver.nations;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Rect;
import com.arckenver.nations.object.Zone;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class Utils
{
	public static final int CLICKER_NONE = 0;
	public static final int CLICKER_DEFAULT = 1;
	public static final int CLICKER_ADMIN = 2;

	// serialization

	public static String locToString(Location<World> loc)
	{
		return loc.getExtent().getName() + "|" + loc.getX() + "|" + loc.getY() + "|" + loc.getZ();
	}

	public static Location<World> locFromString(String str)
	{
		String[] splited = str.split(Pattern.quote("|"));
		if (splited.length != 4)
		{
			NationsPlugin.getLogger().warn("Invalid location format for string " + str);
			return null;
		}
		try
		{
			World world = Sponge.getServer().getWorld(splited[0]).get();
			return world.getLocation(Double.parseDouble(splited[1]), Double.parseDouble(splited[2]), Double.parseDouble(splited[3]));
		}
		catch (NoSuchElementException e)
		{
			NationsPlugin.getLogger().warn("Invalid location format for string " + str);
		}
		catch (NumberFormatException e)
		{
			NationsPlugin.getLogger().warn("Invalid location format for string " + str);
		}
		return null;
	}

	public static String rectToString(Rect rect)
	{
		return rect.getMinX() + ";" + rect.getMaxX() + ";" + rect.getMinY() + ";" + rect.getMaxY();
	}

	public static Rect rectFromString(String str)
	{
		String[] splited = str.split(";");
		return new Rect(null, Integer.parseInt(splited[0]), Integer.parseInt(splited[1]), Integer.parseInt(splited[2]), Integer.parseInt(splited[3]));
	}

	// formatting

	public static Text formatNationDescription(Nation nation, int clicker)
	{
		Builder builder = Text.builder("");
		builder.append(
				Text.of(TextColors.GOLD, "----------{ "),
				Text.of(TextColors.YELLOW,
						((ConfigHandler.getNode("others", "enableNationRanks").getBoolean()) 
								? ConfigHandler.getNationRank(nation.getNumCitizens()).getNode("nationTitle").getString()
										: LanguageHandler.IB)
						+ " - " + nation.getName()),
				Text.of(TextColors.GOLD, " }----------\n"));

		if (!nation.isAdmin())
		{
			BigDecimal balance = null;
			if (NationsPlugin.getEcoService() != null)
			{
				Optional<Account> optAccount = NationsPlugin.getEcoService().getOrCreateAccount("nation-" + nation.getUUID().toString());
				if (optAccount.isPresent())
				{
					balance = optAccount.get().getBalance(NationsPlugin.getEcoService().getDefaultCurrency());
				}
			}
			builder.append(
					Text.of(TextColors.GOLD, LanguageHandler.ID + ": "),
					Text.of(TextColors.YELLOW, nation.getRegion().size() + "/" + nation.maxBlockSize()),
					Text.of(TextColors.GOLD, "\n" + LanguageHandler.IE + ": "),
					((balance == null) ? Text.of(TextColors.GRAY, LanguageHandler.IQ) : formatPrice(TextColors.YELLOW, balance)),
					Text.of(TextColors.GOLD, "\n" + LanguageHandler.IV + ": "),
					formatPrice(TextColors.YELLOW, BigDecimal.valueOf(nation.getTaxes())),
					Text.of(TextColors.GOLD, "\n" + LanguageHandler.IW + ": "),
					formatPrice(TextColors.YELLOW, BigDecimal.valueOf(nation.getUpkeep())),
					Text.of(TextColors.GOLD, "\n" + LanguageHandler.IG + "(", TextColors.YELLOW, + nation.getNumSpawns() + "/" + nation.getMaxSpawns(), TextColors.GOLD, "): ", TextColors.YELLOW, formatNationSpawns(nation, TextColors.YELLOW, clicker)),
					Text.of(TextColors.GOLD, "\n" + LanguageHandler.IH + ": "),
					citizenClickable(TextColors.YELLOW, DataHandler.getPlayerName(nation.getPresident())),
					Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.II + ": "));
			structureX(
					nation.getMinisters().iterator(),
					builder,
					(b) -> b.append(Text.of(TextColors.GRAY, LanguageHandler.IP)),
					(b, uuid) -> b.append(citizenClickable(TextColors.YELLOW, DataHandler.getPlayerName(uuid))),
					(b) -> b.append(Text.of(TextColors.YELLOW, ", ")));
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IJ + ": "));
			structureX(
					nation.getCitizens().iterator(),
					builder,
					(b) -> b.append(Text.of(TextColors.GRAY, LanguageHandler.IP)),
					(b, uuid) -> b.append(citizenClickable(TextColors.YELLOW, DataHandler.getPlayerName(uuid))),
					(b) -> b.append(Text.of(TextColors.YELLOW, ", ")));
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));
		}
		else
		{
			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IY + ": ", TextColors.GREEN, LanguageHandler.JG));
		}

		if (clicker == CLICKER_NONE)
		{
			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IK + ":\n    " + LanguageHandler.IL + ": "));
			builder.append(Text.of((nation.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.JD));
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.of((nation.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.JE));
			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.IJ + ": "));
			builder.append(Text.of((nation.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.JD));
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.of((nation.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.JE));

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IM + ":"));
			for (Entry<String, Boolean> e : nation.getFlags().entrySet())
			{
				builder.append(Text.of(TextColors.GOLD, "\n    " + StringUtils.capitalize(e.getKey().toLowerCase()) + ": "));
				builder.append(Text.of((e.getValue()) ? TextColors.YELLOW : TextColors.DARK_GRAY, LanguageHandler.IT));
				builder.append(Text.of(TextColors.GOLD, "/"));
				builder.append(Text.of((e.getValue()) ? TextColors.DARK_GRAY : TextColors.YELLOW, LanguageHandler.IU));
			}
		}
		else if (clicker == CLICKER_DEFAULT)
		{
			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IK + ":"));
			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.IL + ": "));
			builder.append(Text.builder(LanguageHandler.JD)
					.color((nation.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/n perm " + Nation.TYPE_OUTSIDER + " " + Nation.PERM_BUILD)).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.JE)
					.color((nation.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/n perm " + Nation.TYPE_OUTSIDER + " " + Nation.PERM_INTERACT)).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));

			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.IJ + ": "));
			builder.append(Text.builder(LanguageHandler.JD)
					.color((nation.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/n perm " + Nation.TYPE_CITIZEN + " " + Nation.PERM_BUILD)).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.JE)
					.color((nation.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/n perm " + Nation.TYPE_CITIZEN + " " + Nation.PERM_INTERACT)).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IM + ":"));
			for (Entry<String, Boolean> e : nation.getFlags().entrySet())
			{
				builder.append(Text.of(TextColors.GOLD, "\n    " + StringUtils.capitalize(e.getKey().toLowerCase()) + ": "));
				builder.append(Text.builder(LanguageHandler.IT).color((e.getValue()) ? TextColors.YELLOW : TextColors.DARK_GRAY).onClick(TextActions.runCommand("/n flag " + e.getKey() + " true")).build());
				builder.append(Text.of(TextColors.GOLD, "/"));
				builder.append(Text.builder(LanguageHandler.IU).color((e.getValue()) ? TextColors.DARK_GRAY : TextColors.YELLOW).onClick(TextActions.runCommand("/n flag " + e.getKey() + " false")).build());
				builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));
			}
		}
		else if (clicker == CLICKER_ADMIN)
		{
			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IK + ":"));
			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.IL + ": "));
			builder.append(Text.builder(LanguageHandler.JD)
					.color((nation.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/na perm " + nation.getRealName() + " " + Nation.TYPE_OUTSIDER + " " + Nation.PERM_BUILD)).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.JE)
					.color((nation.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/na perm " + nation.getRealName() + " " + Nation.TYPE_OUTSIDER + " " + Nation.PERM_INTERACT)).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));

			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.IJ + ": "));
			builder.append(Text.builder(LanguageHandler.JD)
					.color((nation.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/na perm " + nation.getRealName() + " " + Nation.TYPE_CITIZEN + " " + Nation.PERM_BUILD)).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.JE)
					.color((nation.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/na perm " + nation.getRealName() + " " + Nation.TYPE_CITIZEN + " " + Nation.PERM_INTERACT)).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IM + ":"));
			for (Entry<String, Boolean> e : nation.getFlags().entrySet())
			{
				builder.append(Text.of(TextColors.GOLD, "\n    " + StringUtils.capitalize(e.getKey().toLowerCase()) + ": "));
				builder.append(Text.builder(LanguageHandler.IT).color((e.getValue()) ? TextColors.YELLOW : TextColors.DARK_GRAY).onClick(TextActions.runCommand("/na flag " + nation.getRealName() + " " + e.getKey() + " true")).build());
				builder.append(Text.of(TextColors.GOLD, "/"));
				builder.append(Text.builder(LanguageHandler.IU).color((e.getValue()) ? TextColors.DARK_GRAY : TextColors.YELLOW).onClick(TextActions.runCommand("/na flag " + nation.getRealName() + " " + e.getKey() + " false")).build());
				builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));
			}
		}

		return builder.build();
	}

	public static Text formatCitizenDescription(String name)
	{
		UUID uuid = DataHandler.getPlayerUUID(name);
		if (uuid == null)
		{
			return Text.of(TextColors.RED, LanguageHandler.IQ);
		}

		Builder builder = Text.builder("");
		builder.append(
				Text.of(TextColors.GOLD, "----------{ "),
				Text.of(TextColors.YELLOW,
						DataHandler.getCitizenTitle(uuid) + " - " + name),
				Text.of(TextColors.GOLD, " }----------")
				);

		BigDecimal balance = null;
		EconomyService service = NationsPlugin.getEcoService();
		if (service != null)
		{
			Optional<UniqueAccount> optAccount = NationsPlugin.getEcoService().getOrCreateAccount(uuid);
			if (optAccount.isPresent())
			{
				balance = optAccount.get().getBalance(NationsPlugin.getEcoService().getDefaultCurrency());
			}
		}
		builder.append(
				Text.of(TextColors.GOLD, "\n" + LanguageHandler.IE + ": "),
				((balance == null) ? Text.of(TextColors.GRAY, LanguageHandler.IQ) : Text.builder()
						.append(Text.of(TextColors.YELLOW, NationsPlugin.getEcoService().getDefaultCurrency().format(balance)))
						.append(Text.of(TextColors.YELLOW, NationsPlugin.getEcoService().getDefaultCurrency().getSymbol()))
						.build())
				);

		builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IB + ": "));
		Nation nation = DataHandler.getNationOfPlayer(uuid);
		if (nation != null)
		{
			builder.append(nationClickable(TextColors.YELLOW, nation.getRealName()));
			if (nation.isPresident(uuid))
			{
				builder.append(Text.of(TextColors.YELLOW, " (" + LanguageHandler.IH + ")"));
			}
			else if (nation.isMinister(uuid))
			{
				builder.append(Text.of(TextColors.YELLOW, " (" + LanguageHandler.II + ")"));
			}

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IZ + ": "));
			boolean ownNothing = true;
			for (Zone zone : nation.getZones().values())
			{
				if (uuid.equals(zone.getOwner()) && zone.isNamed())
				{
					if (ownNothing)
					{
						ownNothing = false;
					}
					else
					{
						builder.append(Text.of(TextColors.YELLOW, ", "));
					}
					builder.append(zoneClickable(TextColors.YELLOW, zone.getRealName()));
				}
			}
			if (ownNothing)
			{
				builder.append(Text.of(TextColors.GRAY, LanguageHandler.IP));
			}
		}
		else
		{
			builder.append(Text.of(TextColors.GRAY, LanguageHandler.IP));
		}

		return builder.build();
	}

	public static Text formatZoneDescription(Zone zone, Nation nation, int clicker)
	{
		Builder builder = Text.builder("");
		UUID owner = zone.getOwner();
		builder.append(
				Text.of(TextColors.GOLD, "----------{ "),
				Text.of(TextColors.YELLOW, "" + LanguageHandler.IC + " - " + zone.getName()),
				Text.of(TextColors.GOLD, " }----------"),
				Text.of(TextColors.GOLD, "\n" + LanguageHandler.IB + ": "),
				Text.of(TextColors.YELLOW, nation.getName()),
				Text.of(TextColors.GOLD, "\n" + LanguageHandler.IN + ": "),
				(owner == null) ? Text.of(TextColors.GRAY, LanguageHandler.IP) : citizenClickable(TextColors.YELLOW, DataHandler.getPlayerName(owner)),
						Text.of(TextColors.GOLD, "\n" + LanguageHandler.IO + ": ")
				);
		structureX(
				zone.getCoowners().iterator(),
				builder,
				(b) -> b.append(Text.of(TextColors.GRAY, LanguageHandler.IP)),
				(b, uuid) -> b.append(citizenClickable(TextColors.YELLOW, DataHandler.getPlayerName(uuid))),
				(b) -> b.append(Text.of(TextColors.YELLOW, ", ")));

		builder.append(
				Text.of(TextColors.GOLD, "\n" + LanguageHandler.IF + ": "),
				(zone.isForSale()) ? formatPrice(TextColors.YELLOW, zone.getPrice()) : Text.of(TextColors.GRAY, LanguageHandler.IR)
				);

		if (clicker == CLICKER_NONE)
		{
			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IK + ":\n    " + LanguageHandler.IL + ": "));
			builder.append(Text.of((zone.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.JD));
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.of((zone.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.JE));
			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.IJ + ": "));
			builder.append(Text.of((zone.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.JD));
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.of((zone.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.JE));
			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.IO + ": "));
			builder.append(Text.of((zone.getPerm(Nation.TYPE_COOWNER, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.JD));
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.of((zone.getPerm(Nation.TYPE_COOWNER, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.JE));

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IM + ":"));
			for (Entry<String, Boolean> e : zone.getFlags().entrySet())
			{
				builder.append(Text.of(TextColors.GOLD, "\n    " + StringUtils.capitalize(e.getKey().toLowerCase()) + ": "));
				builder.append(Text.of((e.getValue()) ? TextColors.YELLOW : TextColors.DARK_GRAY, LanguageHandler.IT));
				builder.append(Text.of(TextColors.GOLD, "/"));
				builder.append(Text.of((e.getValue()) ? TextColors.DARK_GRAY : TextColors.YELLOW, LanguageHandler.IU));
			}
		}
		else if (clicker == CLICKER_DEFAULT)
		{
			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IK + ":"));

			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.IL + ": "));
			builder.append(Text.builder(LanguageHandler.JD)
					.color((zone.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/z perm " + Nation.TYPE_OUTSIDER + " " + Nation.PERM_BUILD)).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.JE)
					.color((zone.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/z perm " + Nation.TYPE_OUTSIDER + " " + Nation.PERM_INTERACT)).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));

			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.IJ + ": "));
			builder.append(Text.builder(LanguageHandler.JD)
					.color((zone.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/z perm " + Nation.TYPE_CITIZEN + " " + Nation.PERM_BUILD)).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.JE)
					.color((zone.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/z perm " + Nation.TYPE_CITIZEN + " " + Nation.PERM_INTERACT)).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));

			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.IO + ": "));
			builder.append(Text.builder(LanguageHandler.JD)
					.color((zone.getPerm(Nation.TYPE_COOWNER, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/z perm " + Nation.TYPE_COOWNER + " " + Nation.PERM_BUILD)).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.JE)
					.color((zone.getPerm(Nation.TYPE_COOWNER, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/z perm " + Nation.TYPE_COOWNER + " " + Nation.PERM_INTERACT)).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IM + ":"));
			for (Entry<String, Boolean> e : zone.getFlags().entrySet())
			{
				builder.append(Text.of(TextColors.GOLD, "\n    " + StringUtils.capitalize(e.getKey().toLowerCase()) + ": "));
				builder.append(Text.builder(LanguageHandler.IT).color((e.getValue()) ? TextColors.YELLOW : TextColors.DARK_GRAY).onClick(TextActions.runCommand("/z flag " + e.getKey() + " true")).build());
				builder.append(Text.of(TextColors.GOLD, "/"));
				builder.append(Text.builder(LanguageHandler.IU).color((e.getValue()) ? TextColors.DARK_GRAY : TextColors.YELLOW).onClick(TextActions.runCommand("/z flag " + e.getKey() + " false")).build());
				builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));
			}
		}

		return builder.build();
	}

	public static Text formatWorldDescription(String name)
	{
		Builder builder = Text.builder("");
		builder.append(
				Text.of(TextColors.GOLD, "----------{ "),
				Text.of(TextColors.YELLOW, name),
				Text.of(TextColors.GOLD, " }----------")
				);

		boolean enabled = ConfigHandler.getNode("worlds").getNode(name).getNode("enabled").getBoolean();

		builder.append(Text.of(TextColors.GOLD, "\nEnabled: "));
		builder.append(Text.builder(LanguageHandler.IT)
				.color((enabled) ? TextColors.YELLOW : TextColors.DARK_GRAY)
				.onClick(TextActions.runCommand("/nw enable " + name)).build());
		builder.append(Text.of(TextColors.GOLD, "/"));
		builder.append(Text.builder(LanguageHandler.IU)
				.color((enabled) ? TextColors.DARK_GRAY : TextColors.YELLOW)
				.onClick(TextActions.runCommand("/nw disable " + name)).build());

		if (!enabled)
		{
			return builder.build();
		}

		builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IK + ": "));

		boolean canBuild = ConfigHandler.getNode("worlds").getNode(name).getNode("perms", "build").getBoolean();
		builder.append(Text.builder(LanguageHandler.JD).color((canBuild) ? TextColors.GREEN : TextColors.RED).onClick(TextActions.runCommand("/nw perm " + Nation.PERM_BUILD)).build());

		builder.append(Text.of(TextColors.GOLD, "/"));

		boolean canInteract = ConfigHandler.getNode("worlds").getNode(name).getNode("perms", "interact").getBoolean();
		builder.append(Text.builder(LanguageHandler.JE).color((canInteract) ? TextColors.GREEN : TextColors.RED).onClick(TextActions.runCommand("/nw perm " + Nation.PERM_INTERACT)).build());

		builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));

		builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.IM + ":"));
		for (Entry<Object, ? extends CommentedConfigurationNode> e : ConfigHandler.getNode("worlds").getNode(name).getNode("flags").getChildrenMap().entrySet())
		{
			String flag = e.getKey().toString();
			boolean b = e.getValue().getBoolean();
			builder.append(Text.of(TextColors.GOLD, "\n    " + StringUtils.capitalize(flag.toLowerCase()) + ": "));
			builder.append(Text.builder(LanguageHandler.IT).color((b) ? TextColors.YELLOW : TextColors.DARK_GRAY).onClick(TextActions.runCommand("/nw flag " + flag + " true")).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.IU).color((b) ? TextColors.DARK_GRAY : TextColors.YELLOW).onClick(TextActions.runCommand("/nw flag " + flag + " false")).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.IX));
		}

		return builder.build();
	}

	public static Text formatPrice(TextColor color, BigDecimal amount)
	{
		return Text.of(color, NationsPlugin.getEcoService().getDefaultCurrency().format(amount));
	}

	public static Text formatNationSpawns(Nation nation, TextColor color)
	{
		return formatNationSpawns(nation, color, CLICKER_DEFAULT);
	}
	
	public static Text formatNationSpawns(Nation nation, TextColor color, int clicker)
	{
		return formatNationSpawns(nation, color, "spawn", clicker);
	}

	public static Text formatNationSpawns(Nation nation, TextColor color, String cmd)
	{
		return formatNationSpawns(nation, color, cmd, CLICKER_DEFAULT);
	}

	public static Text formatNationSpawns(Nation nation, TextColor color, String cmd, int clicker)
	{
		if (clicker == CLICKER_DEFAULT)
		{
			return structureX(
					nation.getSpawns().keySet().iterator(),
					Text.builder(),
					(b) -> b.append(Text.of(TextColors.GRAY, LanguageHandler.IP)),
					(b, spawnName) -> b.append(Text.builder(spawnName).color(color).onClick(TextActions.runCommand("/n " + cmd + " " + spawnName)).build()),
					(b) -> b.append(Text.of(color, ", "))).build();
		}
		if (clicker == CLICKER_ADMIN || nation.getFlag("public"))
		{
			return structureX(
					nation.getSpawns().keySet().iterator(),
					Text.builder(),
					(b) -> b.append(Text.of(TextColors.GRAY, LanguageHandler.IP)),
					(b, spawnName) -> b.append(Text.builder(spawnName).color(color).onClick(TextActions.runCommand("/n visit " + nation.getRealName() + " " + spawnName)).build()),
					(b) -> b.append(Text.of(color, ", "))).build();
		}
		return structureX(
				nation.getSpawns().keySet().iterator(),
				Text.builder(),
				(b) -> b.append(Text.of(TextColors.GRAY, LanguageHandler.IP)),
				(b, spawnName) -> b.append(Text.builder(spawnName).color(color).build()),
				(b) -> b.append(Text.of(color, ", "))).build();

	}

	// clickable

	public static Text nationClickable(TextColor color, String name)
	{
		if (name == null)
		{
			return Text.of(color, LanguageHandler.IQ);
		}
		return Text.builder(name.replace("_", " ")).color(color).onClick(TextActions.runCommand("/n info " + name)).build();
	}

	public static Text citizenClickable(TextColor color, String name)
	{
		if (name == null)
		{
			return Text.of(color, LanguageHandler.IQ);
		}
		return Text.builder(name).color(color).onClick(TextActions.runCommand("/n citizen " + name)).build();
	}

	public static Text zoneClickable(TextColor color, String name)
	{
		if (name == null)
		{
			return Text.of(color, LanguageHandler.IQ);
		}
		return Text.builder(name.replace("_", " ")).color(color).onClick(TextActions.runCommand("/z info " + name)).build();
	}

	public static Text worldClickable(TextColor color, String name)
	{
		if (name == null)
		{
			return Text.of(color, LanguageHandler.IQ);
		}
		return Text.builder(name).color(color).onClick(TextActions.runCommand("/nw info " + name)).build();
	}

	// structure X

	public static <T, U> T structureX(Iterator<U> iter, T obj, Consumer<T> ifNot, BiConsumer<T, U> forEach, Consumer<T> separator)
	{
		if (!iter.hasNext())
		{
			ifNot.accept(obj);
		}
		else
		{
			while (iter.hasNext())
			{
				forEach.accept(obj, iter.next());
				if (iter.hasNext())
				{
					separator.accept(obj);
				}
			}
		}
		return obj;
	}
}
