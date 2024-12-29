package com.arckenver.nations.bukkit.manager

import com.arckenver.nations.bukkit.geometry.Index
import com.arckenver.nations.bukkit.geometry.QuadTreeIndex
import com.arckenver.nations.bukkit.geometry.Rectangle
import com.arckenver.nations.bukkit.geometry.SweepLineArea
import com.arckenver.nations.bukkit.geometry.Vector
import com.arckenver.nations.bukkit.`object`.Nation
import com.arckenver.nations.bukkit.`object`.Reserve
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.`object`.Worldly
import com.arckenver.nations.bukkit.`object`.Zone
import com.arckenver.nations.bukkit.serialization.Json
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import java.util.function.Predicate
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

private fun createIndex(): Index<Territory> = QuadTreeIndex()

sealed class ClaimCheck {
    object CanClaim : ClaimCheck()
    data class CannotClaim(val reason: Reason) : ClaimCheck()

    sealed class Reason {
        data class TooCloseToTerritory(val territory: Territory) : Reason()
        data class NotWithinTerritory(val territory: Territory) : Reason()
    }
}

object TerritoryManager : PersistentManager("territories.json", defaultFileContent = "{}") {
    private val worlds = mutableMapOf<UUID, Index<Territory>>()

    fun addTerritory(rect: Worldly<Rectangle>, territory: Territory) {
        val index = worlds[rect.worldId] ?: createIndex()
        index.insertCut(Index.Entry(rect.value, territory))
        worlds[rect.worldId] = index
        dump()
    }

    fun cutTerritory(rect: Worldly<Rectangle>, territoryPredicate: Predicate<Territory>) {
        val index = worlds[rect.worldId] ?: return
        index.cut(rect.value, territoryPredicate)
        dump()
    }

    fun cutTerritory(rect: Worldly<Rectangle>, territory: Territory) = cutTerritory(rect) { it == territory }

    fun deleteTerritory(territory: Territory) {
        for ((_, index) in worlds) {
            index.search(Index.Query {
                matchValue(territory)
            }).removeAll { true }
        }
        dump()
    }

    fun territoryAt(point: Worldly<Vector>, kind: Territory.Kind? = null) =
        territoriesContaining(point)
            .filter { kind == null || it.kind == kind }
            .sortedWith { a, b -> b.compareTo(a) }
            .firstOrNull()

    fun territoryRectangles(worldId: UUID) =
        queryWorldIndex(worldId, Index.Query { true }).map { it.rect to it.value }

    fun territoriesIntersecting(rect: Worldly<Rectangle>) =
        queryWorldIndex(rect.worldId, Index.Query {
            intersects(rect.value)
        }).map { it.value }

    fun territoriesContaining(point: Worldly<Vector>) =
        queryWorldIndex(point.worldId, Index.Query {
            contains(point.value)
        }).map { it.value }

    fun territoriesAdjacent(rect: Worldly<Rectangle>) =
        territoriesIntersecting(Worldly(rect.worldId, rect.value.expand(1)))

    fun territoriesWithinDistance(rect: Worldly<Rectangle>, distance: Int) =
        queryWorldIndex(rect.worldId, Index.Query {
            withinDistance(rect.value, distance)
        }).map { it.value }

    fun isAdjacent(rect: Worldly<Rectangle>, territory: Territory) =
        territoriesAdjacent(rect).contains(territory)

    fun isIntersecting(rect: Worldly<Rectangle>, territory: Territory) =
        territoriesIntersecting(rect).contains(territory)

    fun isContained(rect: Worldly<Rectangle>, territory: Territory) =
        overlapArea(rect, territory) == rect.value.area

    fun areaOf(territory: Territory, worldId: UUID? = null): Int {
        val indices = if (worldId == null) worlds.values.toList() else listOf(worlds[worldId] ?: return 0)
        val query = Index.Query { matchValue(territory) }
        return indices.sumOf { index ->
            SweepLineArea(index.search(query).map { it.rect }).compute()
        }
    }

    fun overlapArea(rect: Worldly<Rectangle>, territory: Territory) =
        worlds[rect.worldId]
            ?.let { index ->
                index.overlap(rect.value) { it == territory }
            }
            ?: 0

    fun canClaimReserve(rect: Worldly<Rectangle>, reserve: Reserve): ClaimCheck {
        for (territory in territoriesIntersecting(rect)) {
            when (territory.kind) {
                Territory.Kind.RESERVE -> {
                    if (territory.id == reserve.id) {
                        continue
                    }
                    return ClaimCheck.CannotClaim(ClaimCheck.Reason.TooCloseToTerritory(territory))
                }

                Territory.Kind.ZONE -> {
                    continue
                }

                Territory.Kind.NATION -> {
                    return ClaimCheck.CannotClaim(ClaimCheck.Reason.TooCloseToTerritory(territory))
                }
            }
        }
        return ClaimCheck.CanClaim
    }

    fun canClaimNation(rect: Worldly<Rectangle>, nation: Nation): ClaimCheck {
        for (territory in territoriesWithinDistance(rect, ConfigManager.nationClaimMinDistance.get())) {
            when (territory.kind) {
                Territory.Kind.RESERVE -> {
                    return ClaimCheck.CannotClaim(ClaimCheck.Reason.TooCloseToTerritory(territory))
                }

                Territory.Kind.NATION -> {
                    if (territory.id == nation.id) {
                        continue
                    }
                    return ClaimCheck.CannotClaim(ClaimCheck.Reason.TooCloseToTerritory(territory))
                }

                Territory.Kind.ZONE -> {
                    continue
                }
            }
        }
        return ClaimCheck.CanClaim
    }

    fun canClaimZone(rect: Worldly<Rectangle>, zone: Zone): ClaimCheck {
        val nationTerritory = Territory(Territory.Kind.NATION, zone.nationId)
        if (!isContained(rect, nationTerritory)) {
            return ClaimCheck.CannotClaim(
                ClaimCheck.Reason.NotWithinTerritory(nationTerritory)
            )
        }
        for (territory in territoriesIntersecting(rect)) {
            when (territory.kind) {
                Territory.Kind.RESERVE -> {
                    continue
                }

                Territory.Kind.NATION -> {
                    continue
                }

                Territory.Kind.ZONE -> {
                    if (territory.id == zone.id) {
                        continue
                    }
                    return ClaimCheck.CannotClaim(ClaimCheck.Reason.TooCloseToTerritory(territory))
                }
            }
        }
        return ClaimCheck.CanClaim
    }

    private fun queryWorldIndex(
        worldId: UUID,
        query: Index.Query<Territory>
    ): Sequence<Index.Entry<Territory>> {
        val index = worlds[worldId] ?: return sequenceOf()
        return sequence {
            for (entry in index.search(query)) {
                yield(entry)
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun loadFromStream(stream: InputStream) {
        val territories = Json.decodeFromStream<JsonTerritories>(stream)

        for (jsonWorld in territories.worlds) {
            if (jsonWorld.reserves.isEmpty() && jsonWorld.nations.isEmpty()) {
                continue
            }
            val index = worlds[jsonWorld.id] ?: createIndex()

            for (jsonReserve in jsonWorld.reserves) {
                jsonReserve.rects
                    .map {
                        Index.Entry(it, Territory(Territory.Kind.RESERVE, jsonReserve.id))
                    }
                    .forEach { index.insertCut(it) }
            }

            for (jsonNation in jsonWorld.nations) {
                jsonNation.rects
                    .map {
                        Index.Entry(it, Territory(Territory.Kind.NATION, jsonNation.id))
                    }
                    .forEach { index.insertCut(it) }
            }

            jsonWorld.zones
                .map { Index.Entry(it.rect, Territory(Territory.Kind.ZONE, it.id)) }
                .forEach { index.insertCut(it) }

            worlds[jsonWorld.id] = index
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun dumpToStream(stream: OutputStream) {
        val jsonWorlds = mutableMapOf<UUID, JsonWorld>()

        for (world in worlds.entries.sortedBy { it.key }) {
            val jsonReserves = mutableMapOf<UUID, JsonReserve>()
            val jsonNations = mutableMapOf<UUID, JsonNation>()
            val jsonZones = mutableMapOf<UUID, JsonZone>()

            for (entry in world.value.search()) {
                when (entry.value.kind) {
                    Territory.Kind.RESERVE -> {
                        val jsonReserve = jsonReserves[entry.value.id] ?: JsonReserve(entry.value.id)
                        jsonReserve.rects.add(entry.rect)
                        jsonReserves[jsonReserve.id] = jsonReserve
                    }

                    Territory.Kind.NATION -> {
                        val jsonNation = jsonNations[entry.value.id] ?: JsonNation(entry.value.id)
                        jsonNation.rects.add(entry.rect)
                        jsonNations[jsonNation.id] = jsonNation
                    }

                    Territory.Kind.ZONE -> {
                        jsonZones[entry.value.id] = JsonZone(entry.value.id, entry.rect)
                    }
                }
            }

            jsonWorlds[world.key] = JsonWorld(
                world.key,
                jsonReserves.values.toMutableList(),
                jsonNations.values.toMutableList(),
                jsonZones.values.toMutableList(),
            )
        }

        val territories = JsonTerritories(jsonWorlds.values.toMutableList())

        Json.encodeToStream(territories, stream)
    }

    @Serializable
    private data class JsonReserve(
        val id: @Contextual UUID,
        val rects: MutableList<Rectangle> = mutableListOf(),
    )

    @Serializable
    private data class JsonZone(
        val id: @Contextual UUID,
        val rect: Rectangle,
    )

    @Serializable
    private data class JsonNation(
        val id: @Contextual UUID,
        val rects: MutableList<Rectangle> = mutableListOf(),
    )

    @Serializable
    private data class JsonWorld(
        val id: @Contextual UUID,
        val reserves: MutableList<JsonReserve> = mutableListOf(),
        val nations: MutableList<JsonNation> = mutableListOf(),
        val zones: MutableList<JsonZone> = mutableListOf(),
    )

    @Serializable
    private data class JsonTerritories(
        val worlds: MutableList<JsonWorld> = mutableListOf(),
    )
}
