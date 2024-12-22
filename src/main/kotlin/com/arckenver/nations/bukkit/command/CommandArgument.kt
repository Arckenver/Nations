package com.arckenver.nations.bukkit.command

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.ReserveManager
import com.arckenver.nations.bukkit.manager.WorldManager
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.`object`.Balance
import java.util.UUID
import kotlin.enums.enumEntries
import org.bukkit.entity.Player

typealias CommandArgumentParser<T> = (CommandContext, String) -> T?

typealias CommandArgumentTabCompleter = (CommandContext, String) -> List<String>

class CommandArgument<T>(
    val name: String,
    val optional: Boolean,
    val parser: CommandArgumentParser<T>,
    val tabCompleter: CommandArgumentTabCompleter? = null,
) {
    fun parse(ctx: CommandContext, arg: String): T? = parser(ctx, arg)

    fun tabComplete(ctx: CommandContext, arg: String): List<String> =
        tabCompleter?.invoke(ctx, arg) ?: emptyList()


    companion object {
        fun string(name: String, optional: Boolean = false, tabCompleter: CommandArgumentTabCompleter? = null) =
            CommandArgument(
                name,
                optional,
                parser = { _, arg -> arg },
                tabCompleter
            )

        fun uuid(name: String, optional: Boolean = false) =
            CommandArgument(
                name,
                optional,
                parser = { _, arg -> UUID.fromString(arg) },
            )

        fun balance(name: String, optional: Boolean = false, validate: ((Balance) -> Boolean)? = null) =
            CommandArgument(
                name,
                optional,
                parser = { _, arg ->
                    val d = arg.toDoubleOrNull() ?: return@CommandArgument null
                    val b = Balance.fromDouble(d)
                    if (validate != null && !validate(b)) return@CommandArgument null
                    b
                },
            )

        fun int(name: String, optional: Boolean = false) =
            CommandArgument(
                name,
                optional,
                parser = { _, arg -> arg.toIntOrNull() },
            )

        fun boolean(name: String? = null, optional: Boolean = false) =
            CommandArgument(
                name ?: "true/false",
                optional,
                parser = { _, arg ->
                    when (arg.lowercase()) {
                        "true" -> true
                        "false" -> false
                        else -> null
                    }
                },
                tabCompleter = { _, arg ->
                    listOf("true", "false").filter { it.startsWith(arg, ignoreCase = true) }
                }
            )

        inline fun <reified T : Enum<T>> enum(
            name: String? = null,
            optional: Boolean = false,
            entries: Array<T> = enumEntries<T>().toTypedArray(),
        ) =
            CommandArgument(
                name ?: enumEntries<T>().joinToString("/") { it.name.lowercase() },
                optional,
                parser = { _, arg ->
                    entries.find { it.name.equals(arg, ignoreCase = true) }
                },
                tabCompleter = { _, arg ->
                    entries.map { it.name.lowercase() }.filter { it.startsWith(arg, ignoreCase = true) }
                },
            )

        fun reserve(name: String, optional: Boolean = false) =
            CommandArgument(
                name,
                optional,
                parser = { _, arg ->
                    ReserveManager.getReserve(arg)
                },
                tabCompleter = { _, arg ->
                    ReserveManager.listReserves().map { it.name }.filter { it.startsWith(arg, ignoreCase = true) }
                }
            )

        fun nation(name: String, optional: Boolean = false) =
            CommandArgument(
                name,
                optional,
                parser = { _, arg ->
                    NationManager.getNation(arg)
                },
                tabCompleter = { _, arg ->
                    NationManager.listNations().map { it.name }.filter { it.startsWith(arg, ignoreCase = true) }
                }
            )

        fun zone(name: String, optional: Boolean = false) =
            CommandArgument(
                name,
                optional,
                parser = { ctx, arg ->
                    val (_, nation) = ctx.senderPlayerNationMember()
                    ZoneManager.getZone(nation.id, arg)
                },
                tabCompleter = { ctx, arg ->
                    val player = ctx.sender as? Player ?: return@CommandArgument emptyList()
                    val nation = NationManager.getPlayerNation(player.uniqueId) ?: return@CommandArgument emptyList()

                    ZoneManager.listZones(nation.id)
                        .map { it.name }
                        .filter { it.startsWith(arg, ignoreCase = true) }
                }
            )

        fun compatriot(name: String, optional: Boolean = false) =
            CommandArgument(
                name,
                optional,
                parser = { ctx, arg ->
                    val (_, nation) = ctx.senderPlayerNationMember()
                    nation.citizens
                        .map { Nations.plugin.server.getPlayer(it) }
                        .find { it?.name.equals(arg, ignoreCase = true) }
                },
                tabCompleter = { ctx, arg ->
                    val player = ctx.sender as? Player ?: return@CommandArgument emptyList()
                    val nation = NationManager.getPlayerNation(player.uniqueId) ?: return@CommandArgument emptyList()

                    nation.citizens
                        .mapNotNull { Nations.plugin.server.getPlayer(it)?.name }
                        .filter { it.startsWith(arg, ignoreCase = true) }
                }
            )

        fun onlinePlayer(name: String, optional: Boolean = false) =
            CommandArgument(
                name,
                optional,
                parser = { _, arg ->
                    Nations.plugin.server.getPlayer(arg)
                },
                tabCompleter = { _, arg ->
                    Nations.plugin.server.onlinePlayers
                        .map { it.name }
                        .filter { it.startsWith(arg, ignoreCase = true) }
                }
            )

        fun world(name: String, optional: Boolean = false) =
            CommandArgument(
                name,
                optional,
                parser = { _, arg ->
                    WorldManager.getWorld(arg)
                },
                tabCompleter = { _, arg ->
                    WorldManager.listWorlds()
                        .map { it.name }
                        .filter { it.startsWith(arg, ignoreCase = true) }
                }
            )
    }
}
