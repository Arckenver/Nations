package com.arckenver.nations;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Zone;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class Utils
{
	public static final int CLICKER_NONE = 0;
	public static final int CLICKER_DEFAULT = 1;
	public static final int CLICKER_ADMIN = 2;
	
	// players

	private static final String[] FAKE_PLAYERS = {
			"00000000-0000-0000-0000-000000000000",
			"0d0c4ca0-4ff1-11e4-916c-0800200c9a66",
			"41c82c87-7afb-4024-ba57-13d2c99cae77"};
	
	public static boolean isFakePlayer(Player player) {
		String uuid = player.getUniqueId().toString();
		for (int i = 0; i < FAKE_PLAYERS.length; ++i) {
			if (uuid.equals(FAKE_PLAYERS[i]))
				return true;
		}
		return false;
	}
	
	public static boolean isFakePlayer(Event event) {
		return event.getContext().containsKey(EventContextKeys.FAKE_PLAYER);
	}
	
	public static User getUser(Event event) {
		final Cause cause = event.getCause();
        final EventContext context = event.getContext();
        User user = null;
        if (cause != null) {
            user = cause.first(User.class).orElse(null);
        }

        if (user == null) {
            user = context.get(EventContextKeys.NOTIFIER)
                    .orElse(context.get(EventContextKeys.OWNER)
                            .orElse(context.get(EventContextKeys.CREATOR)
                                    .orElse(null)));
        }

        if (user == null) {
            if (event instanceof ExplosionEvent) {
                // Check igniter
                final Living living = context.get(EventContextKeys.IGNITER).orElse(null);
                if (living != null && living instanceof User) {
                    user = (User) living;
                }
            }
        }

        return user;
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
										: LanguageHandler.FORMAT_NATION)
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
					Text.of(TextColors.GOLD, LanguageHandler.FORMAT_SIZE + ": "),
					Text.of(TextColors.YELLOW, nation.getRegion().size() + "/" + nation.maxBlockSize()),
					Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_MONEY + ": "),
					((balance == null) ? Text.of(TextColors.GRAY, LanguageHandler.FORMAT_UNKNOWN) : formatPrice(TextColors.YELLOW, balance)),
					Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_TAXES + ": "),
					formatPrice(TextColors.YELLOW, BigDecimal.valueOf(nation.getTaxes())),
					Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_UPKEEP + ": "),
					formatPrice(TextColors.YELLOW, BigDecimal.valueOf(nation.getUpkeep())),
					Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_SPAWN + "(", TextColors.YELLOW, + nation.getNumSpawns() + "/" + nation.getMaxSpawns(), TextColors.GOLD, "): ", TextColors.YELLOW, formatNationSpawns(nation, TextColors.YELLOW, clicker)),
					Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_PRESIDENT + ": "),
					citizenClickable(TextColors.YELLOW, DataHandler.getPlayerName(nation.getPresident())),
					Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_MINISTERS + ": "));
			structureX(
					nation.getMinisters().iterator(),
					builder,
					(b) -> b.append(Text.of(TextColors.GRAY, LanguageHandler.FORMAT_NONE)),
					(b, uuid) -> b.append(citizenClickable(TextColors.YELLOW, DataHandler.getPlayerName(uuid))),
					(b) -> b.append(Text.of(TextColors.YELLOW, ", ")));
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_CITIZENS + ": "));
			structureX(
					nation.getCitizens().iterator(),
					builder,
					(b) -> b.append(Text.of(TextColors.GRAY, LanguageHandler.FORMAT_NONE)),
					(b, uuid) -> b.append(citizenClickable(TextColors.YELLOW, DataHandler.getPlayerName(uuid))),
					(b) -> b.append(Text.of(TextColors.YELLOW, ", ")));
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));
		}
		else
		{
			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_ADMIN + ": ", TextColors.GREEN, LanguageHandler.VALUE_TRUE));
			if (nation.getNumSpawns() > 0)
			{
				builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_SPAWN + ": ", TextColors.YELLOW, formatNationSpawns(nation, TextColors.YELLOW, clicker)));
			}
		}

		if (clicker == CLICKER_NONE)
		{
			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_PERMISSIONS + ":\n    " + LanguageHandler.FORMAT_OUTSIDERS + ": "));
			builder.append(Text.of((nation.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.TYPE_BUILD));
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.of((nation.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.TYPE_INTERACT));
			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.FORMAT_CITIZENS + ": "));
			builder.append(Text.of((nation.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.TYPE_BUILD));
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.of((nation.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.TYPE_INTERACT));

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_FLAGS + ":"));
			for (Entry<String, Boolean> e : nation.getFlags().entrySet())
			{
				builder.append(Text.of(TextColors.GOLD, "\n    " + StringUtils.capitalize(e.getKey().toLowerCase()) + ": "));
				builder.append(Text.of((e.getValue()) ? TextColors.YELLOW : TextColors.DARK_GRAY, LanguageHandler.FLAG_ENABLED));
				builder.append(Text.of(TextColors.GOLD, "/"));
				builder.append(Text.of((e.getValue()) ? TextColors.DARK_GRAY : TextColors.YELLOW, LanguageHandler.FLAG_DISABLED));
			}
		}
		else if (clicker == CLICKER_DEFAULT)
		{
			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_PERMISSIONS + ":"));
			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.FORMAT_OUTSIDERS + ": "));
			builder.append(Text.builder(LanguageHandler.TYPE_BUILD)
					.color((nation.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/n perm " + Nation.TYPE_OUTSIDER + " " + Nation.PERM_BUILD)).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.TYPE_INTERACT)
					.color((nation.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/n perm " + Nation.TYPE_OUTSIDER + " " + Nation.PERM_INTERACT)).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));

			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.FORMAT_CITIZENS + ": "));
			builder.append(Text.builder(LanguageHandler.TYPE_BUILD)
					.color((nation.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/n perm " + Nation.TYPE_CITIZEN + " " + Nation.PERM_BUILD)).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.TYPE_INTERACT)
					.color((nation.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/n perm " + Nation.TYPE_CITIZEN + " " + Nation.PERM_INTERACT)).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_FLAGS + ":"));
			for (Entry<String, Boolean> e : nation.getFlags().entrySet())
			{
				builder.append(Text.of(TextColors.GOLD, "\n    " + StringUtils.capitalize(e.getKey().toLowerCase()) + ": "));
				builder.append(Text.builder(LanguageHandler.FLAG_ENABLED).color((e.getValue()) ? TextColors.YELLOW : TextColors.DARK_GRAY).onClick(TextActions.runCommand("/n flag " + e.getKey() + " true")).build());
				builder.append(Text.of(TextColors.GOLD, "/"));
				builder.append(Text.builder(LanguageHandler.FLAG_DISABLED).color((e.getValue()) ? TextColors.DARK_GRAY : TextColors.YELLOW).onClick(TextActions.runCommand("/n flag " + e.getKey() + " false")).build());
				builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));
			}
		}
		else if (clicker == CLICKER_ADMIN)
		{
			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_PERMISSIONS + ":"));
			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.FORMAT_OUTSIDERS + ": "));
			builder.append(Text.builder(LanguageHandler.TYPE_BUILD)
					.color((nation.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/na perm " + nation.getRealName() + " " + Nation.TYPE_OUTSIDER + " " + Nation.PERM_BUILD)).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.TYPE_INTERACT)
					.color((nation.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/na perm " + nation.getRealName() + " " + Nation.TYPE_OUTSIDER + " " + Nation.PERM_INTERACT)).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));

			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.FORMAT_CITIZENS + ": "));
			builder.append(Text.builder(LanguageHandler.TYPE_BUILD)
					.color((nation.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/na perm " + nation.getRealName() + " " + Nation.TYPE_CITIZEN + " " + Nation.PERM_BUILD)).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.TYPE_INTERACT)
					.color((nation.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/na perm " + nation.getRealName() + " " + Nation.TYPE_CITIZEN + " " + Nation.PERM_INTERACT)).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_FLAGS + ":"));
			for (Entry<String, Boolean> e : nation.getFlags().entrySet())
			{
				builder.append(Text.of(TextColors.GOLD, "\n    " + StringUtils.capitalize(e.getKey().toLowerCase()) + ": "));
				builder.append(Text.builder(LanguageHandler.FLAG_ENABLED).color((e.getValue()) ? TextColors.YELLOW : TextColors.DARK_GRAY).onClick(TextActions.runCommand("/na flag " + nation.getRealName() + " " + e.getKey() + " true")).build());
				builder.append(Text.of(TextColors.GOLD, "/"));
				builder.append(Text.builder(LanguageHandler.FLAG_DISABLED).color((e.getValue()) ? TextColors.DARK_GRAY : TextColors.YELLOW).onClick(TextActions.runCommand("/na flag " + nation.getRealName() + " " + e.getKey() + " false")).build());
				builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));
			}
		}

		return builder.build();
	}

	public static Text formatCitizenDescription(String name)
	{
		UUID uuid = DataHandler.getPlayerUUID(name);
		if (uuid == null)
		{
			return Text.of(TextColors.RED, LanguageHandler.FORMAT_UNKNOWN);
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
				Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_MONEY + ": "),
				((balance == null) ? Text.of(TextColors.GRAY, LanguageHandler.FORMAT_UNKNOWN) : Text.builder()
						.append(Text.of(TextColors.YELLOW, NationsPlugin.getEcoService().getDefaultCurrency().format(balance)))
						.append(Text.of(TextColors.YELLOW, NationsPlugin.getEcoService().getDefaultCurrency().getSymbol()))
						.build())
				);

		builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_NATION + ": "));
		Nation nation = DataHandler.getNationOfPlayer(uuid);
		if (nation != null)
		{
			builder.append(nationClickable(TextColors.YELLOW, nation.getRealName()));
			if (nation.isPresident(uuid))
			{
				builder.append(Text.of(TextColors.YELLOW, " (" + LanguageHandler.FORMAT_PRESIDENT + ")"));
			}
			else if (nation.isMinister(uuid))
			{
				builder.append(Text.of(TextColors.YELLOW, " (" + LanguageHandler.FORMAT_MINISTERS + ")"));
			}

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_ZONES + ": "));
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
				builder.append(Text.of(TextColors.GRAY, LanguageHandler.FORMAT_NONE));
			}
		}
		else
		{
			builder.append(Text.of(TextColors.GRAY, LanguageHandler.FORMAT_NONE));
		}

		return builder.build();
	}

	public static Text formatZoneDescription(Zone zone, Nation nation, int clicker)
	{
		Builder builder = Text.builder("");
		UUID owner = zone.getOwner();
		builder.append(
				Text.of(TextColors.GOLD, "----------{ "),
				Text.of(TextColors.YELLOW, "" + LanguageHandler.FORMAT_ZONE + " - " + zone.getName()),
				Text.of(TextColors.GOLD, " }----------"),
				Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_NATION + ": "),
				Text.of(TextColors.YELLOW, nation.getName()),
				Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_OWNER + ": "),
				(owner == null) ? Text.of(TextColors.GRAY, LanguageHandler.FORMAT_NONE) : citizenClickable(TextColors.YELLOW, DataHandler.getPlayerName(owner)),
						Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_COOWNER + ": ")
				);
		structureX(
				zone.getCoowners().iterator(),
				builder,
				(b) -> b.append(Text.of(TextColors.GRAY, LanguageHandler.FORMAT_NONE)),
				(b, uuid) -> b.append(citizenClickable(TextColors.YELLOW, DataHandler.getPlayerName(uuid))),
				(b) -> b.append(Text.of(TextColors.YELLOW, ", ")));

		builder.append(
				Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_PRICE + ": "),
				(zone.isForSale()) ? formatPrice(TextColors.YELLOW, zone.getPrice()) : Text.of(TextColors.GRAY, LanguageHandler.FORMAT_NFS)
				);

		if (clicker == CLICKER_NONE)
		{
			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_PERMISSIONS + ":\n    " + LanguageHandler.FORMAT_OUTSIDERS + ": "));
			builder.append(Text.of((zone.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.TYPE_BUILD));
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.of((zone.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.TYPE_INTERACT));
			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.FORMAT_CITIZENS + ": "));
			builder.append(Text.of((zone.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.TYPE_BUILD));
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.of((zone.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.TYPE_INTERACT));
			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.FORMAT_COOWNER + ": "));
			builder.append(Text.of((zone.getPerm(Nation.TYPE_COOWNER, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.TYPE_BUILD));
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.of((zone.getPerm(Nation.TYPE_COOWNER, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED, LanguageHandler.TYPE_INTERACT));

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_FLAGS + ":"));
			for (Entry<String, Boolean> e : zone.getFlags().entrySet())
			{
				builder.append(Text.of(TextColors.GOLD, "\n    " + StringUtils.capitalize(e.getKey().toLowerCase()) + ": "));
				builder.append(Text.of((e.getValue()) ? TextColors.YELLOW : TextColors.DARK_GRAY, LanguageHandler.FLAG_ENABLED));
				builder.append(Text.of(TextColors.GOLD, "/"));
				builder.append(Text.of((e.getValue()) ? TextColors.DARK_GRAY : TextColors.YELLOW, LanguageHandler.FLAG_DISABLED));
			}
		}
		else if (clicker == CLICKER_DEFAULT)
		{
			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_PERMISSIONS + ":"));

			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.FORMAT_OUTSIDERS + ": "));
			builder.append(Text.builder(LanguageHandler.TYPE_BUILD)
					.color((zone.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/z perm " + Nation.TYPE_OUTSIDER + " " + Nation.PERM_BUILD)).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.TYPE_INTERACT)
					.color((zone.getPerm(Nation.TYPE_OUTSIDER, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/z perm " + Nation.TYPE_OUTSIDER + " " + Nation.PERM_INTERACT)).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));

			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.FORMAT_CITIZENS + ": "));
			builder.append(Text.builder(LanguageHandler.TYPE_BUILD)
					.color((zone.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/z perm " + Nation.TYPE_CITIZEN + " " + Nation.PERM_BUILD)).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.TYPE_INTERACT)
					.color((zone.getPerm(Nation.TYPE_CITIZEN, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/z perm " + Nation.TYPE_CITIZEN + " " + Nation.PERM_INTERACT)).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));

			builder.append(Text.of(TextColors.GOLD, "\n    " + LanguageHandler.FORMAT_COOWNER + ": "));
			builder.append(Text.builder(LanguageHandler.TYPE_BUILD)
					.color((zone.getPerm(Nation.TYPE_COOWNER, Nation.PERM_BUILD)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/z perm " + Nation.TYPE_COOWNER + " " + Nation.PERM_BUILD)).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.TYPE_INTERACT)
					.color((zone.getPerm(Nation.TYPE_COOWNER, Nation.PERM_INTERACT)) ? TextColors.GREEN : TextColors.RED)
					.onClick(TextActions.runCommand("/z perm " + Nation.TYPE_COOWNER + " " + Nation.PERM_INTERACT)).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));

			builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_FLAGS + ":"));
			for (Entry<String, Boolean> e : zone.getFlags().entrySet())
			{
				builder.append(Text.of(TextColors.GOLD, "\n    " + StringUtils.capitalize(e.getKey().toLowerCase()) + ": "));
				builder.append(Text.builder(LanguageHandler.FLAG_ENABLED).color((e.getValue()) ? TextColors.YELLOW : TextColors.DARK_GRAY).onClick(TextActions.runCommand("/z flag " + e.getKey() + " true")).build());
				builder.append(Text.of(TextColors.GOLD, "/"));
				builder.append(Text.builder(LanguageHandler.FLAG_DISABLED).color((e.getValue()) ? TextColors.DARK_GRAY : TextColors.YELLOW).onClick(TextActions.runCommand("/z flag " + e.getKey() + " false")).build());
				builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));
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
		builder.append(Text.builder(LanguageHandler.FLAG_ENABLED)
				.color((enabled) ? TextColors.YELLOW : TextColors.DARK_GRAY)
				.onClick(TextActions.runCommand("/nw enable " + name)).build());
		builder.append(Text.of(TextColors.GOLD, "/"));
		builder.append(Text.builder(LanguageHandler.FLAG_DISABLED)
				.color((enabled) ? TextColors.DARK_GRAY : TextColors.YELLOW)
				.onClick(TextActions.runCommand("/nw disable " + name)).build());

		if (!enabled)
		{
			return builder.build();
		}

		builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_PERMISSIONS + ": "));

		boolean canBuild = ConfigHandler.getNode("worlds").getNode(name).getNode("perms", "build").getBoolean();
		builder.append(Text.builder(LanguageHandler.TYPE_BUILD).color((canBuild) ? TextColors.GREEN : TextColors.RED).onClick(TextActions.runCommand("/nw perm " + Nation.PERM_BUILD)).build());

		builder.append(Text.of(TextColors.GOLD, "/"));

		boolean canInteract = ConfigHandler.getNode("worlds").getNode(name).getNode("perms", "interact").getBoolean();
		builder.append(Text.builder(LanguageHandler.TYPE_INTERACT).color((canInteract) ? TextColors.GREEN : TextColors.RED).onClick(TextActions.runCommand("/nw perm " + Nation.PERM_INTERACT)).build());

		builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));

		builder.append(Text.of(TextColors.GOLD, "\n" + LanguageHandler.FORMAT_FLAGS + ":"));
		for (Entry<Object, ? extends CommentedConfigurationNode> e : ConfigHandler.getNode("worlds").getNode(name).getNode("flags").getChildrenMap().entrySet())
		{
			String flag = e.getKey().toString();
			boolean b = e.getValue().getBoolean();
			builder.append(Text.of(TextColors.GOLD, "\n    " + StringUtils.capitalize(flag.toLowerCase()) + ": "));
			builder.append(Text.builder(LanguageHandler.FLAG_ENABLED).color((b) ? TextColors.YELLOW : TextColors.DARK_GRAY).onClick(TextActions.runCommand("/nw flag " + flag + " true")).build());
			builder.append(Text.of(TextColors.GOLD, "/"));
			builder.append(Text.builder(LanguageHandler.FLAG_DISABLED).color((b) ? TextColors.DARK_GRAY : TextColors.YELLOW).onClick(TextActions.runCommand("/nw flag " + flag + " false")).build());
			builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.CLICK));
		}

		return builder.build();
	}

	public static Text formatPrice(TextColor color, BigDecimal amount)
	{
		return Text.of(color, NationsPlugin.getEcoService().getDefaultCurrency().format(amount));
	}

	public static String formatPricePlain(BigDecimal amount)
	{
		return TextSerializers.FORMATTING_CODE.serialize(NationsPlugin.getEcoService().getDefaultCurrency().format(amount));
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
					(b) -> b.append(Text.of(TextColors.GRAY, LanguageHandler.FORMAT_NONE)),
					(b, spawnName) -> b.append(Text.builder(spawnName).color(color).onClick(TextActions.runCommand("/n " + cmd + " " + spawnName)).build()),
					(b) -> b.append(Text.of(color, ", "))).build();
		}
		if (clicker == CLICKER_ADMIN || nation.getFlag("public"))
		{
			return structureX(
					nation.getSpawns().keySet().iterator(),
					Text.builder(),
					(b) -> b.append(Text.of(TextColors.GRAY, LanguageHandler.FORMAT_NONE)),
					(b, spawnName) -> b.append(Text.builder(spawnName).color(color).onClick(TextActions.runCommand("/n visit " + nation.getRealName() + " " + spawnName)).build()),
					(b) -> b.append(Text.of(color, ", "))).build();
		}
		return structureX(
				nation.getSpawns().keySet().iterator(),
				Text.builder(),
				(b) -> b.append(Text.of(TextColors.GRAY, LanguageHandler.FORMAT_NONE)),
				(b, spawnName) -> b.append(Text.builder(spawnName).color(color).build()),
				(b) -> b.append(Text.of(color, ", "))).build();

	}

	// clickable

	public static Text nationClickable(TextColor color, String name)
	{
		if (name == null)
		{
			return Text.of(color, LanguageHandler.FORMAT_UNKNOWN);
		}
		return Text.builder(name.replace("_", " ")).color(color).onClick(TextActions.runCommand("/n info " + name)).build();
	}

	public static Text citizenClickable(TextColor color, String name)
	{
		if (name == null)
		{
			return Text.of(color, LanguageHandler.FORMAT_UNKNOWN);
		}
		return Text.builder(name).color(color).onClick(TextActions.runCommand("/n citizen " + name)).build();
	}

	public static Text zoneClickable(TextColor color, String name)
	{
		if (name == null)
		{
			return Text.of(color, LanguageHandler.FORMAT_UNKNOWN);
		}
		return Text.builder(name.replace("_", " ")).color(color).onClick(TextActions.runCommand("/z info " + name)).build();
	}

	public static Text worldClickable(TextColor color, String name)
	{
		if (name == null)
		{
			return Text.of(color, LanguageHandler.FORMAT_UNKNOWN);
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
