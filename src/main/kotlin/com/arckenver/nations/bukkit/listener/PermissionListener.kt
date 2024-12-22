package com.arckenver.nations.bukkit.listener

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.geometry.Vector
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.manager.WorldManager
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.`object`.Action
import com.arckenver.nations.bukkit.`object`.Actor
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.`object`.Worldly
import com.arckenver.nations.bukkit.text.Text
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

object PermissionListener : Listener {
    @EventHandler
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        val player = event.player
        val blockPoint = Nations.locationPoint(event.block.location)

        if (!canBuild(player, blockPoint)) {
            event.isCancelled = true
            Nations.sendActionBar(player, Text.t("nations.cannot_build").red())
        }
    }

    @EventHandler
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val player = event.player
        val blockPoint = Nations.locationPoint(event.block.location)

        if (!canBuild(player, blockPoint)) {
            event.isCancelled = true
            Nations.sendActionBar(player, Text.t("nations.cannot_build").red())
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        when (event.action) {
            org.bukkit.event.block.Action.LEFT_CLICK_AIR,
            org.bukkit.event.block.Action.RIGHT_CLICK_AIR -> {
                return
            }

            org.bukkit.event.block.Action.LEFT_CLICK_BLOCK,
            org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK,
            org.bukkit.event.block.Action.PHYSICAL -> {
                val player = event.player
                val clickedBlock = event.clickedBlock ?: return
                val blockPoint = Nations.locationPoint(clickedBlock.location)

                if (!canInteract(player, blockPoint)) {
                    event.isCancelled = true
                    Nations.sendActionBar(player, Text.t("nations.cannot_interact").red())
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val player = event.player
        val blockPoint = Nations.locationPoint(event.rightClicked.location)

        if (!canInteract(player, blockPoint)) {
            event.isCancelled = true
            Nations.sendActionBar(player, Text.t("nations.cannot_interact").red())
        }
    }

    @EventHandler
    fun onHangingBreak(event: HangingBreakByEntityEvent) {
        val player = event.remover as? Player ?: return
        val blockPoint = Nations.locationPoint(event.entity.location)

        if (!canInteract(player, blockPoint)) {
            event.isCancelled = true
            Nations.sendActionBar(player, Text.t("nations.cannot_build").red())
        }
    }

    private fun canBuild(player: Player, point: Worldly<Vector>) = canPerformAction(
        player,
        point,
        Action.BUILD,
        Nations.permissionAdminBypassPermBuild,
    )

    private

    fun canInteract(player: Player, point: Worldly<Vector>) = canPerformAction(
        player,
        point,
        Action.INTERACT,
        Nations.permissionAdminBypassPermInteract,
    )

    private fun canPerformAction(
        player: Player,
        point: Worldly<Vector>,
        action: Action,
        bypassPermission: String,
    ): Boolean {
        if (player.hasPermission(bypassPermission)) {
            return true
        }

        val territory = TerritoryManager.territoryAt(point)
        return if (territory == null) {
            val world = WorldManager.getWorld(point.worldId)
            if (world == null) {
                true
            } else {
                world.getPermission(action)
            }

        } else when (territory.kind) {
            Territory.Kind.RESERVE -> {
                false
            }

            Territory.Kind.NATION -> {
                val nation = NationManager.getNation(territory.id)!!
                when {
                    nation.isStaff(player.uniqueId) -> true
                    nation.isCitizen(player.uniqueId) -> nation.getPermission(Actor.CITIZEN, action)
                    else -> nation.getPermission(Actor.OUTSIDER, action)
                }
            }

            Territory.Kind.ZONE -> {
                val zone = ZoneManager.getZone(territory.id)!!
                val nation = NationManager.getNation(zone.nationId)!!
                when {
                    zone.isOwner(player.uniqueId) || nation.isStaff(player.uniqueId) -> true
                    zone.isCoowner(player.uniqueId) -> zone.getPermission(Actor.COOWNER, action)
                    nation.isCitizen(player.uniqueId) -> zone.getPermission(Actor.CITIZEN, action)
                    else -> zone.getPermission(Actor.OUTSIDER, action)
                }
            }
        }
    }
}