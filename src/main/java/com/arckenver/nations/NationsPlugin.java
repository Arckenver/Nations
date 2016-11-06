package com.arckenver.nations;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import com.arckenver.nations.cmdelement.CitizenNameElement;
import com.arckenver.nations.cmdelement.NationNameElement;
import com.arckenver.nations.cmdelement.PlayerNameElement;
import com.arckenver.nations.cmdelement.WorldNameElement;
import com.arckenver.nations.cmdelement.ZoneNameElement;
import com.arckenver.nations.cmdexecutor.nation.NationBuyextraExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationCitizenExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationClaimExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationClaimOutpostExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationCreateExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationDelspawnExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationDepositExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationFlagExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationHereExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationInfoExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationInviteExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationJoinExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationKickExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationLeaveExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationListExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationMinisterExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationPermExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationResignExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationSetspawnExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationSpawnExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationTaxesExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationUnclaimExecutor;
import com.arckenver.nations.cmdexecutor.nation.NationWithdrawExecutor;
import com.arckenver.nations.cmdexecutor.nationadmin.NationadminClaimExecutor;
import com.arckenver.nations.cmdexecutor.nationadmin.NationadminCreateExecutor;
import com.arckenver.nations.cmdexecutor.nationadmin.NationadminDeleteExecutor;
import com.arckenver.nations.cmdexecutor.nationadmin.NationadminEcoExecutor;
import com.arckenver.nations.cmdexecutor.nationadmin.NationadminExecutor;
import com.arckenver.nations.cmdexecutor.nationadmin.NationadminFlagExecutor;
import com.arckenver.nations.cmdexecutor.nationadmin.NationadminForcejoinExecutor;
import com.arckenver.nations.cmdexecutor.nationadmin.NationadminForceleaveExecutor;
import com.arckenver.nations.cmdexecutor.nationadmin.NationadminPermExecutor;
import com.arckenver.nations.cmdexecutor.nationadmin.NationadminReloadExecutor;
import com.arckenver.nations.cmdexecutor.nationadmin.NationadminSetnameExecutor;
import com.arckenver.nations.cmdexecutor.nationadmin.NationadminSetpresExecutor;
import com.arckenver.nations.cmdexecutor.nationworld.NationworldDisableExecutor;
import com.arckenver.nations.cmdexecutor.nationworld.NationworldEnableExecutor;
import com.arckenver.nations.cmdexecutor.nationworld.NationworldExecutor;
import com.arckenver.nations.cmdexecutor.nationworld.NationworldFlagExecutor;
import com.arckenver.nations.cmdexecutor.nationworld.NationworldInfoExecutor;
import com.arckenver.nations.cmdexecutor.nationworld.NationworldListExecutor;
import com.arckenver.nations.cmdexecutor.nationworld.NationworldPermExecutor;
import com.arckenver.nations.cmdexecutor.zone.ZoneBuyExecutor;
import com.arckenver.nations.cmdexecutor.zone.ZoneCoownerExecutor;
import com.arckenver.nations.cmdexecutor.zone.ZoneCreateExecutor;
import com.arckenver.nations.cmdexecutor.zone.ZoneDeleteExecutor;
import com.arckenver.nations.cmdexecutor.zone.ZoneDelownerExecutor;
import com.arckenver.nations.cmdexecutor.zone.ZoneExecutor;
import com.arckenver.nations.cmdexecutor.zone.ZoneFlagExecutor;
import com.arckenver.nations.cmdexecutor.zone.ZoneInfoExecutor;
import com.arckenver.nations.cmdexecutor.zone.ZoneListExecutor;
import com.arckenver.nations.cmdexecutor.zone.ZonePermExecutor;
import com.arckenver.nations.cmdexecutor.zone.ZoneSellExecutor;
import com.arckenver.nations.cmdexecutor.zone.ZoneSetownerExecutor;
import com.arckenver.nations.listener.BuildPermListener;
import com.arckenver.nations.listener.ExplosionListener;
import com.arckenver.nations.listener.FireListener;
import com.arckenver.nations.listener.GoldenAxeListener;
import com.arckenver.nations.listener.InteractPermListener;
import com.arckenver.nations.listener.MobSpawningListener;
import com.arckenver.nations.listener.PlayerConnectionListener;
import com.arckenver.nations.listener.PlayerMoveListener;
import com.arckenver.nations.listener.PvpListener;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.service.NationsService;
import com.arckenver.nations.task.TaxesCollectRunnable;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

@Plugin(id = "com.arckenver.nations", name = "Nations", version = "1.0", description = "A towny-like worldguard-like zone managment plugin.")
public class NationsPlugin
{
	private File rootDir;

	private static NationsPlugin plugin;

	@Inject
	private Logger logger;

	@Inject
	@ConfigDir(sharedRoot = true)
	private File defaultConfigDir;

	private EconomyService economyService = null;

	@Listener
	public void onInit(GameInitializationEvent event)
	{
		plugin = this;

		rootDir = new File(defaultConfigDir, "nations");

		LanguageHandler.init(rootDir);
		ConfigHandler.init(rootDir);
		DataHandler.init(rootDir);

		Sponge.getServiceManager().setProvider(this, NationsService.class, new NationsService());
	}

	@Listener
	public void onStart(GameStartedServerEvent event)
	{
		LanguageHandler.load();
		ConfigHandler.load();
		DataHandler.load();
		
		Sponge.getServiceManager()
				.getRegistration(EconomyService.class)
				.ifPresent(prov -> economyService = prov.getProvider());

		CommandSpec nationadminReloadCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.reload")
				.arguments()
				.executor(new NationadminReloadExecutor())
				.build();
		
		CommandSpec nationadminCreateCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.create")
				.arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
				.executor(new NationadminCreateExecutor())
				.build();
		
		CommandSpec nationadminClaimCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.claim")
				.arguments(GenericArguments.optional(GenericArguments.string(Text.of("nation"))))
				.executor(new NationadminClaimExecutor())
				.build();
		
		CommandSpec nationadminSetpresCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.setpres")
				.arguments(
						GenericArguments.optional(new NationNameElement(Text.of("nation"))),
						GenericArguments.optional(new PlayerNameElement(Text.of("president"))))
				.executor(new NationadminSetpresExecutor())
				.build();

		CommandSpec nationadminSetnameCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.setname")
				.arguments(
						GenericArguments.optional(new NationNameElement(Text.of("oldname"))),
						GenericArguments.optional(GenericArguments.string(Text.of("newname"))))
				.executor(new NationadminSetnameExecutor())
				.build();

		CommandSpec nationadminForcejoinCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.forcejoin")
				.arguments(
						GenericArguments.optional(new NationNameElement(Text.of("nation"))),
						GenericArguments.optional(new PlayerNameElement(Text.of("player"))))
				.executor(new NationadminForcejoinExecutor())
				.build();
		
		CommandSpec nationadminForceleaveCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.forceleave")
				.arguments(GenericArguments.optional(new PlayerNameElement(Text.of("player"))))
				.executor(new NationadminForceleaveExecutor())
				.build();
		
		CommandSpec nationadminEcoCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.eco")
				.arguments(
						GenericArguments.optional(GenericArguments.choices(Text.of("give|take|set"),
								ImmutableMap.<String, String> builder()
										.put("give", "give")
										.put("take", "take")
										.put("set", "set")
										.build())),
						GenericArguments.optional(new NationNameElement(Text.of("nation"))),
						GenericArguments.optional(GenericArguments.doubleNum(Text.of("amount"))))
				.executor(new NationadminEcoExecutor())
				.build();
		
		CommandSpec nationadminDeleteCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.delete")
				.arguments(GenericArguments.optional(new NationNameElement(Text.of("nation"))))
				.executor(new NationadminDeleteExecutor())
				.build();
		
		CommandSpec nationadminFlagCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.flag")
				.arguments(
						GenericArguments.optional(new NationNameElement(Text.of("nation"))),
						GenericArguments.optional(GenericArguments.choices(Text.of("flag"), ConfigHandler.getNode("nations", "flags")
								.getChildrenMap()
								.keySet()
								.stream()
								.map(key -> key.toString())
								.collect(Collectors.toMap(flag -> flag, flag -> flag)))),
						GenericArguments.optional(GenericArguments.bool(Text.of("bool"))))
				.executor(new NationadminFlagExecutor())
				.build();
		
		CommandSpec nationadminPermCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.perm")
				.arguments(
						GenericArguments.optional(new NationNameElement(Text.of("nation"))),
						GenericArguments.optional(GenericArguments.choices(Text.of("type"),
								ImmutableMap.<String, String> builder()
										.put(Nation.TYPE_OUTSIDER, Nation.TYPE_OUTSIDER)
										.put(Nation.TYPE_CITIZEN, Nation.TYPE_CITIZEN)
										.put(Nation.TYPE_COOWNER, Nation.TYPE_COOWNER)
										.build())),
						GenericArguments.optional(GenericArguments.choices(Text.of("perm"),
								ImmutableMap.<String, String> builder()
										.put(Nation.PERM_BUILD, Nation.PERM_BUILD)
										.put(Nation.PERM_INTERACT, Nation.PERM_INTERACT)
										.build())),
						GenericArguments.optional(GenericArguments.bool(Text.of("bool"))))
				.executor(new NationadminPermExecutor())
				.build();
		
		CommandSpec nationadminCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin")
				.executor(new NationadminExecutor())
				.child(nationadminReloadCmd, "reload")
				.child(nationadminCreateCmd, "create")
				.child(nationadminClaimCmd, "claim")
				.child(nationadminSetpresCmd, "setpres", "setpresident")
				.child(nationadminSetnameCmd, "setname")
				.child(nationadminForcejoinCmd, "forcejoin")
				.child(nationadminForceleaveCmd, "forceleave")
				.child(nationadminEcoCmd, "eco")
				.child(nationadminDeleteCmd, "delete")
				.child(nationadminFlagCmd, "flag")
				.child(nationadminPermCmd, "perm")
				.build();

		CommandSpec nationInfoCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.info")
				.arguments(GenericArguments.optional(new NationNameElement(Text.of("nation"))))
				.executor(new NationInfoExecutor())
				.build();

		CommandSpec nationHereCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.here")
				.arguments()
				.executor(new NationHereExecutor())
				.build();

		CommandSpec nationListCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.list")
				.arguments()
				.executor(new NationListExecutor())
				.build();

		CommandSpec nationCitizenCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.citizen")
				.arguments(GenericArguments.optional(new PlayerNameElement(Text.of("player"))))
				.executor(new NationCitizenExecutor())
				.build();

		CommandSpec nationCreateCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.create")
				.arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
				.executor(new NationCreateExecutor())
				.build();

		CommandSpec nationDepositCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.deposit")
				.arguments(GenericArguments.optional(GenericArguments.doubleNum(Text.of("amount"))))
				.executor(new NationDepositExecutor())
				.build();

		CommandSpec nationWithdrawCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.withdraw")
				.arguments(GenericArguments.optional(GenericArguments.doubleNum(Text.of("amount"))))
				.executor(new NationWithdrawExecutor())
				.build();

		CommandSpec nationCreateOutpostCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.claim.outpost")
				.arguments()
				.executor(new NationClaimOutpostExecutor())
				.build();

		CommandSpec nationClaimCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.claim")
				.arguments()
				.executor(new NationClaimExecutor())
				.child(nationCreateOutpostCmd, "outpost", "o")
				.build();

		CommandSpec nationUnclaimCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.unclaim")
				.arguments()
				.executor(new NationUnclaimExecutor())
				.build();

		CommandSpec nationInviteCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.invite")
				.arguments(GenericArguments.optional(GenericArguments.player(Text.of("player"))))
				.executor(new NationInviteExecutor())
				.build();

		CommandSpec nationJoinCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.join")
				.arguments(GenericArguments.optional(new NationNameElement(Text.of("nation"))))
				.executor(new NationJoinExecutor())
				.build();

		CommandSpec nationKickCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.kick")
				.arguments(GenericArguments.optional(new CitizenNameElement(Text.of("player"))))
				.executor(new NationKickExecutor())
				.build();

		CommandSpec nationLeaveCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.leave")
				.arguments()
				.executor(new NationLeaveExecutor())
				.build();

		CommandSpec nationResignCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.resign")
				.arguments(GenericArguments.optional(new CitizenNameElement(Text.of("successor"))))
				.executor(new NationResignExecutor())
				.build();

		CommandSpec nationSpawnCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.spawn")
				.arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
				.executor(new NationSpawnExecutor())
				.build();

		CommandSpec nationSetspawnCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.setspawn")
				.arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
				.executor(new NationSetspawnExecutor())
				.build();

		CommandSpec nationDelspawnCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.delspawn")
				.arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
				.executor(new NationDelspawnExecutor())
				.build();

		CommandSpec nationBuyextraCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.buyextra")
				.arguments(GenericArguments.optional(GenericArguments.integer(Text.of("amount"))))
				.executor(new NationBuyextraExecutor())
				.build();

		CommandSpec nationTaxesCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.taxes")
				.arguments(GenericArguments.optional(GenericArguments.doubleNum(Text.of("amount"))))
				.executor(new NationTaxesExecutor())
				.build();

		CommandSpec nationMinisterCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.minister")
				.arguments(
						GenericArguments.optional(GenericArguments.choices(Text.of("add|remove"),
								ImmutableMap.<String, String> builder()
										.put("add", "add")
										.put("remove", "remove")
										.build())),
						GenericArguments.optional(new CitizenNameElement(Text.of("citizen"))))
				.executor(new NationMinisterExecutor())
				.build();

		CommandSpec nationPermCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.perm")
				.arguments(
						GenericArguments.choices(Text.of("type"),
								ImmutableMap.<String, String> builder()
										.put(Nation.TYPE_OUTSIDER, Nation.TYPE_OUTSIDER)
										.put(Nation.TYPE_CITIZEN, Nation.TYPE_CITIZEN)
										.build()),
						GenericArguments.choices(Text.of("perm"),
								ImmutableMap.<String, String> builder()
										.put(Nation.PERM_BUILD, Nation.PERM_BUILD)
										.put(Nation.PERM_INTERACT, Nation.PERM_INTERACT)
										.build()),
						GenericArguments.optional(GenericArguments.bool(Text.of("bool"))))
				.executor(new NationPermExecutor())
				.build();

		CommandSpec nationFlagCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.flag")
				.arguments(
						GenericArguments.choices(Text.of("flag"), ConfigHandler.getNode("nations", "flags")
								.getChildrenMap()
								.keySet()
								.stream()
								.map(key -> key.toString())
								.collect(Collectors.toMap(flag -> flag, flag -> flag))),
						GenericArguments.optional(GenericArguments.bool(Text.of("bool"))))
				.executor(new NationFlagExecutor())
				.build();

		CommandSpec nationCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation")
				.executor(new NationExecutor())
				.child(nationInfoCmd, "info")
				.child(nationHereCmd, "here", "h")
				.child(nationListCmd, "list", "l")
				.child(nationCitizenCmd, "citizen", "whois")
				.child(nationCreateCmd, "create", "new")
				.child(nationDepositCmd, "deposit", "give")
				.child(nationWithdrawCmd, "withdraw", "take")
				.child(nationClaimCmd, "claim")
				.child(nationUnclaimCmd, "unclaim")
				.child(nationInviteCmd, "invite", "add")
				.child(nationJoinCmd, "join", "apply")
				.child(nationKickCmd, "kick")
				.child(nationLeaveCmd, "leave", "quit")
				.child(nationResignCmd, "resign")
				.child(nationSpawnCmd, "spawn")
				.child(nationSetspawnCmd, "setspawn")
				.child(nationDelspawnCmd, "delspawn")
				.child(nationBuyextraCmd, "buyextra")
				.child(nationTaxesCmd, "taxes")
				.child(nationMinisterCmd, "minister")
				.child(nationPermCmd, "perm")
				.child(nationFlagCmd, "flag")
				.build();

		CommandSpec zoneInfoCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.info")
				.arguments(GenericArguments.optional(GenericArguments.string(Text.of("zone"))))
				.executor(new ZoneInfoExecutor())
				.build();

		CommandSpec zoneListCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.list")
				.arguments(GenericArguments.optional(new NationNameElement(Text.of("nation"))))
				.executor(new ZoneListExecutor())
				.build();

		CommandSpec zoneCreateCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.create")
				.arguments(
						GenericArguments.optional(GenericArguments.string(Text.of("name"))),
						GenericArguments.optional(new CitizenNameElement(Text.of("owner"))))
				.executor(new ZoneCreateExecutor())
				.build();

		CommandSpec zoneDeleteCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.delete")
				.arguments(GenericArguments.optional(new ZoneNameElement(Text.of("zone"))))
				.executor(new ZoneDeleteExecutor())
				.build();

		CommandSpec zoneCoownerCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.coowner")
				.arguments(
						GenericArguments.optional(GenericArguments.choices(Text.of("add|remove"),
								ImmutableMap.<String, String> builder()
										.put("add", "add")
										.put("remove", "remove")
										.build())),
						GenericArguments.optional(new CitizenNameElement(Text.of("citizen"))))
				.executor(new ZoneCoownerExecutor())
				.build();

		CommandSpec zoneSetownerCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.setowner")
				.arguments(GenericArguments.optional(new CitizenNameElement(Text.of("owner"))))
				.executor(new ZoneSetownerExecutor())
				.build();

		CommandSpec zoneDelownerCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.delowner")
				.arguments()
				.executor(new ZoneDelownerExecutor())
				.build();

		CommandSpec zonePermCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.perm")
				.arguments(
						GenericArguments.choices(Text.of("type"),
								ImmutableMap.<String, String> builder()
										.put(Nation.TYPE_OUTSIDER, Nation.TYPE_OUTSIDER)
										.put(Nation.TYPE_CITIZEN, Nation.TYPE_CITIZEN)
										.put(Nation.TYPE_COOWNER, Nation.TYPE_COOWNER)
										.build()),
						GenericArguments.choices(Text.of("perm"),
								ImmutableMap.<String, String> builder()
										.put(Nation.PERM_BUILD, Nation.PERM_BUILD)
										.put(Nation.PERM_INTERACT, Nation.PERM_INTERACT)
										.build()),
						GenericArguments.optional(GenericArguments.bool(Text.of("bool"))))
				.executor(new ZonePermExecutor())
				.build();

		CommandSpec zoneFlagCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.flag")
				.arguments(
						GenericArguments.choices(Text.of("flag"), ConfigHandler.getNode("nations", "flags")
								.getChildrenMap()
								.keySet()
								.stream()
								.map(o -> o.toString())
								.collect(Collectors.toMap(flag -> flag, flag -> flag))),
						GenericArguments.optional(GenericArguments.bool(Text.of("bool"))))
				.executor(new ZoneFlagExecutor())
				.build();

		CommandSpec zoneSellCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.sell")
				.arguments(GenericArguments.optional(GenericArguments.doubleNum(Text.of("price"))))
				.executor(new ZoneSellExecutor())
				.build();

		CommandSpec zoneBuyCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.buy")
				.arguments()
				.executor(new ZoneBuyExecutor())
				.build();

		CommandSpec zoneCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone")
				.executor(new ZoneExecutor())
				.child(zoneInfoCmd, "info")
				.child(zoneListCmd, "list")
				.child(zoneCreateCmd, "create", "add")
				.child(zoneDeleteCmd, "delete", "remove")
				.child(zoneCoownerCmd, "coowner")
				.child(zoneSetownerCmd, "setowner")
				.child(zoneDelownerCmd, "delowner")
				.child(zonePermCmd, "perm")
				.child(zoneFlagCmd, "flag")
				.child(zoneSellCmd, "sell", "forsale", "fs")
				.child(zoneBuyCmd, "buy", "claim")
				.build();

		CommandSpec worldInfoCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationworld.info")
				.arguments(GenericArguments.optional(new WorldNameElement(Text.of("world"))))
				.executor(new NationworldInfoExecutor())
				.build();

		CommandSpec worldListCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationworld.list")
				.arguments()
				.executor(new NationworldListExecutor())
				.build();
		
		CommandSpec worldEnableCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationworld.enable")
				.arguments(GenericArguments.optional(new WorldNameElement(Text.of("world"))))
				.executor(new NationworldEnableExecutor())
				.build();
		
		CommandSpec worldDisableCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationworld.disable")
				.arguments(GenericArguments.optional(new WorldNameElement(Text.of("world"))))
				.executor(new NationworldDisableExecutor())
				.build();

		CommandSpec worldPermCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationworld.perm")
				.arguments(
						GenericArguments.choices(Text.of("perm"),
								ImmutableMap.<String, String> builder()
										.put(Nation.PERM_BUILD, Nation.PERM_BUILD)
										.put(Nation.PERM_INTERACT, Nation.PERM_INTERACT)
										.build()),
						GenericArguments.optional(GenericArguments.bool(Text.of("bool"))))
				.executor(new NationworldPermExecutor())
				.build();

		CommandSpec worldFlagCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationworld.flag")
				.arguments(
						GenericArguments.choices(Text.of("flag"), ConfigHandler.getNode("nations", "flags")
								.getChildrenMap()
								.keySet()
								.stream()
								.map(o -> o.toString())
								.collect(Collectors.toMap(flag -> flag, flag -> flag))),
						GenericArguments.optional(GenericArguments.bool(Text.of("bool"))))
				.executor(new NationworldFlagExecutor())
				.build();
		
		CommandSpec nationworldCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationworld")
				.executor(new NationworldExecutor())
				.child(worldInfoCmd, "info")
				.child(worldListCmd, "list")
				.child(worldEnableCmd, "enable")
				.child(worldDisableCmd, "disable")
				.child(worldPermCmd, "perm")
				.child(worldFlagCmd, "flag")
				.build();

		Sponge.getCommandManager().register(this, nationadminCmd, "nationadmin", "na", "nationsadmin");
		Sponge.getCommandManager().register(this, nationCmd, "nation", "n", "nations");
		Sponge.getCommandManager().register(this, zoneCmd, "zone", "z");
		Sponge.getCommandManager().register(this, nationworldCmd, "nationworld", "nw");

		Sponge.getEventManager().registerListeners(this, new PlayerConnectionListener());
		Sponge.getEventManager().registerListeners(this, new PlayerMoveListener());
		Sponge.getEventManager().registerListeners(this, new GoldenAxeListener());
		Sponge.getEventManager().registerListeners(this, new PvpListener());
		Sponge.getEventManager().registerListeners(this, new FireListener());
		Sponge.getEventManager().registerListeners(this, new ExplosionListener());
		Sponge.getEventManager().registerListeners(this, new MobSpawningListener());
		Sponge.getEventManager().registerListeners(this, new BuildPermListener());
		Sponge.getEventManager().registerListeners(this, new InteractPermListener());

		LocalDateTime localNow = LocalDateTime.now();
		ZonedDateTime zonedNow = ZonedDateTime.of(localNow, ZoneId.systemDefault());
		ZonedDateTime zonedNext = zonedNow.withHour(12).withMinute(0).withSecond(0);
		if (zonedNow.compareTo(zonedNext) > 0)
			zonedNext = zonedNext.plusDays(1);
		long initalDelay = Duration.between(zonedNow, zonedNext).getSeconds();

		Sponge.getScheduler()
				.createTaskBuilder()
				.execute(new TaxesCollectRunnable())
				.delay(initalDelay, TimeUnit.SECONDS)
				.interval(1, TimeUnit.DAYS)
				.async()
				.submit(this);

		logger.info("Plugin ready");
	}

	@Listener
	public void onServerStopping(GameStoppingServerEvent event)
	{
		logger.info("Saving data");
		ConfigHandler.save();
		DataHandler.save();
		logger.info("Plugin stopped");
	}

	@Listener
	public void onChangeServiceProvider(ChangeServiceProviderEvent event)
	{
		if (event.getService().equals(EconomyService.class))
		{
			economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
		}
	}

	public static NationsPlugin getInstance()
	{
		return plugin;
	}

	public static Logger getLogger()
	{
		return getInstance().logger;
	}

	public static EconomyService getEcoService()
	{
		return getInstance().economyService;
	}

	public static Cause getCause()
	{
		return Cause.source(NationsPlugin.getInstance()).build();
	}
}
