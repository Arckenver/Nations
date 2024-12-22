package com.arckenver.nations.bukkit

import com.arckenver.nations.bukkit.command.nation.NationCommand
import com.arckenver.nations.bukkit.command.nationadmin.NationadminCommand
import com.arckenver.nations.bukkit.command.zone.ZoneCommand
import com.arckenver.nations.bukkit.geometry.Vector
import com.arckenver.nations.bukkit.listener.ChatListener
import com.arckenver.nations.bukkit.listener.FlagListener
import com.arckenver.nations.bukkit.listener.MapListener
import com.arckenver.nations.bukkit.listener.PermissionListener
import com.arckenver.nations.bukkit.listener.SelectionListener
import com.arckenver.nations.bukkit.listener.TerritoryActionBarListener
import com.arckenver.nations.bukkit.manager.ConfigManager
import com.arckenver.nations.bukkit.manager.ConfirmationManager
import com.arckenver.nations.bukkit.manager.InviteManager
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.ReserveManager
import com.arckenver.nations.bukkit.manager.TaxManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.manager.WorldManager
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.`object`.Nation
import com.arckenver.nations.bukkit.`object`.Worldly
import com.arckenver.nations.bukkit.text.Text
import com.arckenver.nations.bukkit.text.Textable
import com.arckenver.nations.bukkit.text.UnknownText
import java.io.File
import java.util.Locale
import java.util.ResourceBundle
import java.util.UUID
import kotlin.reflect.KClass
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import net.kyori.adventure.util.UTF8ResourceBundleControl
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.anjocaido.groupmanager.GroupManager
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class ProviderNotFoundException(kclass: KClass<*>, pluginName: String) :
    Exception("Could not find provider ${kclass.qualifiedName}, check you have installed plugin $pluginName")

object Nations {
    lateinit var plugin: NationsPlugin
    private lateinit var adventure: BukkitAudiences
    lateinit var vaultEconomy: Economy
    var vaultPermission: Permission? = null
    var groupManager: GroupManager? = null

    val dataDir: File by lazy { createDataDirIfNotExists() }
    private var dataLoaded = false
    private val persistentManagers = listOf(WorldManager, NationManager, ZoneManager, ReserveManager, TerritoryManager)
    private val taskTimerAsyncManagers = listOf(ConfirmationManager, InviteManager, TaxManager)
    private val listeners =
        listOf(
            SelectionListener,
            TerritoryActionBarListener,
            PermissionListener,
            FlagListener,
            MapListener,
            ChatListener
        )
    private val commands = listOf(NationCommand, ZoneCommand, NationadminCommand)

    val permissionAdminBypassPermBuild = "nations.admin.bypass.build"
    val permissionAdminBypassPermInteract = "nations.admin.bypass.interact"

    init {
        registerTranslations()
    }

    fun onEnable(plugin: NationsPlugin) {
        this.plugin = plugin
        this.adventure = BukkitAudiences.create(plugin)

        try {
            this.vaultEconomy = getProvider<Economy>("Vault")
        } catch (e: ProviderNotFoundException) {
            e.printStackTrace()
            plugin.server.pluginManager.disablePlugin(plugin)
        }

        try {
            this.vaultPermission = getProvider<Permission>("Vault")
            this.groupManager = plugin.server.pluginManager.getPlugin("GroupManager")
                ?.takeIf { it.isEnabled }
                ?.let { it as GroupManager }

        } catch (_: ProviderNotFoundException) {
            plugin.logger.warning("Could not find Vault permission provider, chat formatting might not work as intended")
        }

        loadData()
        scheduleTasks()
        registerListeners()
        registerCommands()
    }

    fun onDisable() {
        dumpData()
    }

    private fun registerTranslations() {
        val registry = TranslationRegistry.create(Key.key("namespace:value"))

        val bundle = ResourceBundle.getBundle("messages", Locale.US, UTF8ResourceBundleControl.get())
        registry.registerAll(Locale.US, bundle, true)
        GlobalTranslator.translator().addSource(registry)
    }

    private inline fun <reified T> getProvider(providerPluginName: String): T =
        plugin.server.servicesManager.getRegistration(T::class.java)?.provider
            ?: throw ProviderNotFoundException(T::class, providerPluginName)

    private fun loadData() {
        ConfigManager.load()
        persistentManagers.forEach { it.load() }
        dataLoaded = true
    }

    private fun dumpData() {
        ConfigManager.save()
        if (!dataLoaded) {
            return
        }
        persistentManagers.forEach { it.dump() }
    }

    private fun scheduleTasks() {
        taskTimerAsyncManagers.forEach { it.schedule(plugin) }
    }

    private fun registerListeners() {
        listeners.forEach { plugin.server.pluginManager.registerEvents(it, plugin) }
    }

    private fun registerCommands() {
        commands.forEach { it.register(plugin) }
    }

    private fun createDataDirIfNotExists(): File {
        val dir = File(plugin.dataFolder, "data")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun playerName(playerId: UUID) = plugin.server.getOfflinePlayer(playerId).name?.let { Text(it) } ?: UnknownText

    fun sendMessageIfOnline(playerId: UUID, message: Textable) =
        plugin.server.getPlayer(playerId)?.let { sendMessage(it, message) }

    fun sendMessage(sender: CommandSender, message: Textable) = sendMessage(adventure.sender(sender), message)

    fun sendMessage(player: Player, message: Textable) = sendMessage(adventure.player(player), message)

    private fun sendMessage(audience: Audience, message: Textable) {
        audience.sendMessage(message)
    }

    fun broadcastMessage(message: Textable) {
        plugin.server.onlinePlayers.forEach { sendMessage(it, message) }
        sendMessage(plugin.server.consoleSender, message)
    }

    fun broadcastMessageNation(nation: Nation, message: Textable) {
        nation.citizens.forEach { sendMessageIfOnline(it, message) }
    }

    fun sendActionBar(player: Player, message: Textable) = sendActionBar(adventure.player(player), message)

    private fun sendActionBar(audience: Audience, message: Textable) {
        audience.sendActionBar(message)
    }

    fun locationPoint(loc: Location) = Worldly(
        loc.world!!.uid,
        Vector(loc.blockX, loc.blockZ)
    )
}

class NationsPlugin : JavaPlugin() {
    override fun onEnable() {
        Nations.onEnable(this)
    }

    override fun onDisable() {
        Nations.onDisable()
    }
}
