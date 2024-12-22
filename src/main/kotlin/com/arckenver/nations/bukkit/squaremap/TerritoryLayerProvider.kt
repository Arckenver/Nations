package com.arckenver.nations.bukkit.squaremap

import com.arckenver.nations.bukkit.geometry.Rectangle
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.ReserveManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.`object`.Territory
import net.kyori.adventure.text.format.NamedTextColor
import xyz.jpenilla.squaremap.api.Key
import xyz.jpenilla.squaremap.api.LayerProvider
import xyz.jpenilla.squaremap.api.Point
import xyz.jpenilla.squaremap.api.marker.Marker
import xyz.jpenilla.squaremap.api.marker.MarkerOptions
import java.awt.Color
import java.util.*

class TerritoryLayerProvider(val worldId: UUID) : LayerProvider {
    companion object {
        val key = Key.of("nations.territory_layer_provider")
    }

    override fun getLabel() = "Nations Territories"

    override fun layerPriority() = 1

    override fun getMarkers(): Collection<Marker?> {
        return TerritoryManager
            .territoryRectangles(worldId)
            .map { (rect: Rectangle, territory: Territory) ->
                Marker
                    .rectangle(
                        Point.of(rect.x1.toDouble(), rect.z1.toDouble()),
                        Point.of(rect.x2.toDouble(), rect.z2.toDouble())
                    )
                    .markerOptions(
                        MarkerOptions.builder()
                            .stroke(false)
                            .fillColor(territoryColor(territory))
                            .fillOpacity(0.4)
                            .hoverTooltip(territoryHoverTooltip(territory))
                            .build()
                    )
            }
            .toList()
    }

    private fun territoryColor(territory: Territory) = when (territory.kind) {
        Territory.Kind.RESERVE -> jwtColorFromNamedTextColor(NamedTextColor.GOLD)
        Territory.Kind.NATION -> jwtColorFromNamedTextColor(NamedTextColor.DARK_AQUA)
        Territory.Kind.ZONE -> jwtColorFromNamedTextColor(NamedTextColor.DARK_PURPLE)
    }

    private fun territoryHoverTooltip(territory: Territory) = when (territory.kind) {
        Territory.Kind.RESERVE -> {
            val reserve = ReserveManager.getReserve(territory.id)!!
            reserve.name
        }

        Territory.Kind.NATION -> {
            val nation = NationManager.getNation(territory.id)!!
            nation.name
        }

        Territory.Kind.ZONE -> {
            val zone = ZoneManager.getZone(territory.id)!!
            val nation = NationManager.getNation(zone.nationId)!!
            "${nation.name} - ${zone.name}"
        }
    }

    private fun jwtColorFromNamedTextColor(c: NamedTextColor) = Color(c.red(), c.green(), c.blue())
}
