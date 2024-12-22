package com.arckenver.nations.bukkit.listener

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.geometry.Vector
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.manager.WorldManager
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.`object`.Flag
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.`object`.Worldly
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityExplodeEvent

object FlagListener : Listener {
    @EventHandler
    fun onBlockIgnite(event: BlockIgniteEvent) {
        val point = Nations.locationPoint(event.block.location)
        if (!hasFlag(point, Flag.FIRE)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        val point = Nations.locationPoint(event.location)
        if (!hasFlag(point, Flag.EXPLOSIONS)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent) {
        val point = Nations.locationPoint(event.block.location)
        if (!hasFlag(point, Flag.EXPLOSIONS)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        val point = Nations.locationPoint(event.location)
        if (mobEntityTypes.contains(event.entity.type) && !hasFlag(point, Flag.MOBS)) {
            event.isCancelled = true
        }
    }

    @Suppress("UnstableApiUsage")
    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.entity is Player && (event.damager is Player || event.damageSource.causingEntity is Player)) {
            val point = Nations.locationPoint(event.entity.location)
            if (!hasFlag(point, Flag.PVP)) {
                event.isCancelled = true
            }
        }
    }

    fun hasFlag(point: Worldly<Vector>, flag: Flag): Boolean {
        val territory = TerritoryManager.territoryAt(point)

        return if (territory == null) {
            val world = WorldManager.getWorld(point.worldId)
            if (world == null) {
                true
            } else {
                world.getFlag(flag)
            }

        } else when (territory.kind) {
            Territory.Kind.RESERVE -> {
                false
            }

            Territory.Kind.NATION -> {
                NationManager.getNation(territory.id)!!.getFlag(flag)
            }

            Territory.Kind.ZONE -> {
                ZoneManager.getZone(territory.id)!!.getFlag(flag)
            }
        }
    }
}

private val mobEntityTypes = listOf(
    EntityType.ELDER_GUARDIAN,
    EntityType.WITHER_SKELETON,
    EntityType.STRAY,
    EntityType.HUSK,
    EntityType.ZOMBIE_VILLAGER,
    EntityType.EVOKER,
    EntityType.VEX,
    EntityType.VINDICATOR,
    EntityType.ILLUSIONER,
    EntityType.CREEPER,
    EntityType.SKELETON,
    EntityType.SPIDER,
    EntityType.GIANT,
    EntityType.ZOMBIE,
    EntityType.SLIME,
    EntityType.GHAST,
    EntityType.ZOMBIFIED_PIGLIN,
    EntityType.ENDERMAN,
    EntityType.CAVE_SPIDER,
    EntityType.SILVERFISH,
    EntityType.BLAZE,
    EntityType.MAGMA_CUBE,
    EntityType.ENDER_DRAGON,
    EntityType.WITHER,
    EntityType.WITCH,
    EntityType.ENDERMITE,
    EntityType.GUARDIAN,
    EntityType.SHULKER,
    EntityType.PHANTOM,
    EntityType.DROWNED,
    EntityType.PILLAGER,
    EntityType.RAVAGER,
    EntityType.HOGLIN,
    EntityType.PIGLIN,
    EntityType.ZOGLIN,
    EntityType.PIGLIN_BRUTE,
    EntityType.WARDEN,
    EntityType.BOGGED,
)
