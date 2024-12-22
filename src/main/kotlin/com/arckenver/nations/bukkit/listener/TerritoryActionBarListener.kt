package com.arckenver.nations.bukkit.listener

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.ReserveManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.text.Text
import com.arckenver.nations.bukkit.text.UnknownText
import java.util.UUID
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

object TerritoryActionBarListener : Listener {
    private val lastPlayerTerritory = mutableMapOf<UUID, Territory>()

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val toLoc = event.to ?: return
        if (event.from.blockX == toLoc.blockX && event.from.blockZ == toLoc.blockZ && event.from.world == toLoc.world) {
            return
        }

        val toPoint = Nations.locationPoint(toLoc)
        val toTerritory = TerritoryManager.territoryAt(toPoint)
        val lastTerritory = lastPlayerTerritory[event.player.uniqueId]

        if (lastTerritory == toTerritory) {
            return
        }

        if (toTerritory == null) {
            lastPlayerTerritory.remove(event.player.uniqueId)

            Nations.sendActionBar(event.player, Text.t("nations.wilderness").darkGreen())

        } else {
            lastPlayerTerritory[event.player.uniqueId] = toTerritory

            when (toTerritory.kind) {
                Territory.Kind.RESERVE -> {
                    Nations.sendActionBar(event.player, ReserveManager.getReserve(toTerritory.id) ?: UnknownText)
                }

                Territory.Kind.NATION -> {
                    Nations.sendActionBar(event.player, NationManager.getNation(toTerritory.id) ?: UnknownText)
                }

                Territory.Kind.ZONE -> {
                    val zone = ZoneManager.getZone(toTerritory.id)
                    val nation = zone?.let { NationManager.getNation(it.nationId) }
                    val nationZone = Text.nationZone(
                        nation ?: UnknownText,
                        zone ?: UnknownText,
                    )

                    val price = zone?.price

                    val text = if (price != null) {
                        nationZone + Text(" - ") + price.toText().yellow()
                    } else {
                        nationZone
                    }

                    Nations.sendActionBar(event.player, text)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val territory = TerritoryManager.territoryAt(Nations.locationPoint(event.player.location)) ?: return
        lastPlayerTerritory[event.player.uniqueId] = territory
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        lastPlayerTerritory.remove(event.player.uniqueId)
    }
}