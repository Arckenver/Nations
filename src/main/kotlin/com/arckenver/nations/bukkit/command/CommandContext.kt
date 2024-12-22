package com.arckenver.nations.bukkit.command

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.geometry.Rectangle
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.SelectionManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.`object`.Nation
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.`object`.Worldly
import com.arckenver.nations.bukkit.`object`.Zone
import com.arckenver.nations.bukkit.text.Text
import com.arckenver.nations.bukkit.text.Textable
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandException(val msg: Text) : Exception() {
    companion object {
        fun t(key: String, vararg args: Textable) = CommandException(Text.t(key, *args))
    }
}

class CommandContext(val sender: CommandSender) {
    private var argumentValues = mutableMapOf<String, Any>()

    fun <T : Any> argument(arg: CommandArgument<T>): T {
        val value = argumentValues[arg.name]!!
        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    fun <T : Any> optionalArgument(arg: CommandArgument<T>): T? {
        val value = argumentValues[arg.name]
        @Suppress("UNCHECKED_CAST")
        return value as? T
    }

    fun setArgument(arg: CommandArgument<*>, value: Any) {
        argumentValues[arg.name] = value
    }

    @Throws(CommandException::class)
    fun senderPlayer(): Player {
        return sender as? Player ?: throw CommandException.t("nations.must_be_player")
    }

    @Throws(CommandException::class)
    fun senderPlayerNationMember(): Pair<Player, Nation> {
        val player = senderPlayer()
        val nation =
            NationManager.getPlayerNation(player.uniqueId) ?: throw CommandException.t("nations.must_belong_to_nation")
        return player to nation
    }

    @Throws(CommandException::class)
    fun senderPlayerNationStaff(): Pair<Player, Nation> {
        val (player, nation) = senderPlayerNationMember()
        if (!nation.isStaff(player.uniqueId)) {
            throw CommandException.t("nations.must_be_staff")
        }
        return player to nation
    }

    @Throws(CommandException::class)
    fun senderPlayerNationPresident(): Pair<Player, Nation> {
        val (player, nation) = senderPlayerNationMember()
        if (!nation.isPresident(player.uniqueId)) {
            throw CommandException.t("nations.must_be_president")
        }
        return player to nation
    }

    @Throws(CommandException::class)
    fun senderSelection(): Worldly<Rectangle> {
        val player = senderPlayer()
        return SelectionManager.selection(player.uniqueId) ?: throw CommandException.t(
            "nations.must_select_rect",
            SelectionManager.selectionItemText
        )
    }

    @Throws(CommandException::class)
    fun senderPlayerNoNation(): Player {
        val player = senderPlayer()
        if (NationManager.getPlayerNation(player.uniqueId) != null) {
            throw CommandException.t("nations.already_belong_to_nation")
        }
        return player
    }

    @Throws(CommandException::class)
    fun senderZoneAtPlayerLocation(): Zone {
        val player = senderPlayer()

        val territory = TerritoryManager.territoryAt(
            Nations.locationPoint(player.location),
            Territory.Kind.ZONE
        ) ?: throw CommandException.t("nations.no_zone_here")

        return ZoneManager.getZone(territory.id)!!
    }
}
