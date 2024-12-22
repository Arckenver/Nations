package com.arckenver.nations.bukkit.listener

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.squaremap.TerritoryLayerProvider
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.event.world.WorldUnloadEvent
import xyz.jpenilla.squaremap.api.BukkitAdapter
import xyz.jpenilla.squaremap.api.SquaremapProvider

object MapListener : Listener {
    @EventHandler
    fun onServerLoad(event: ServerLoadEvent) {
        for (world in Nations.plugin.server.worlds) {
            registerWorld(world)
        }
    }

    @EventHandler
    fun onWorldLoad(event: WorldLoadEvent) {
        registerWorld(event.world)
    }

    @EventHandler
    fun onWorldUnload(event: WorldUnloadEvent) {
        unregisterWorld(event.world)
    }

    private fun registerWorld(world: World) {
        val provider = squaremapProvider() ?: return
        val squaremapWorld = provider.getWorldIfEnabled(BukkitAdapter.worldIdentifier(world)).orElse(null) ?: return

        val layerProvider = TerritoryLayerProvider(world.uid)
        squaremapWorld.layerRegistry().register(TerritoryLayerProvider.key, layerProvider)
    }

    private fun unregisterWorld(world: World) {
        val provider = squaremapProvider() ?: return
        val squaremapWorld = provider.getWorldIfEnabled(BukkitAdapter.worldIdentifier(world)).orElse(null) ?: return

        squaremapWorld.layerRegistry().unregister(TerritoryLayerProvider.key)
    }

    private fun squaremapProvider() =
        try {
            SquaremapProvider.get()
        } catch (_: IllegalStateException) {
            null
        }
}