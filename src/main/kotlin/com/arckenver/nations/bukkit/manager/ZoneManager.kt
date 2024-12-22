package com.arckenver.nations.bukkit.manager

import com.arckenver.nations.bukkit.`object`.Nation
import com.arckenver.nations.bukkit.`object`.Zone
import com.arckenver.nations.bukkit.serialization.Json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.collections.forEach

object ZoneManager : PersistentManager("zones.json", defaultFileContent = "{}") {
    private val zones: MutableMap<UUID, Zone> = mutableMapOf()

    fun getZone(zoneId: UUID): Zone? = zones[zoneId]

    fun getZone(nationId: UUID, zoneName: String): Zone? =
        zones.values.find { it.nationId == nationId && it.name.equals(zoneName, ignoreCase = true) }

    fun listZones(nationId: UUID): Iterable<Zone> = zones.values.filter { it.nationId == nationId }

    fun createZone(zone: Zone) {
        if (zones[zone.id] != null) {
            throw IllegalArgumentException("Zone ${zone.id} already exists")
        }

        zones[zone.id] = zone
        dump()
    }

    fun deleteZone(id: UUID) {
        zones.remove(id)?.let { dump() }
    }

    fun deleteZones(nationId: UUID) {
        val iter = zones.values.iterator()
        while (iter.hasNext()) {
            if (iter.next().nationId == nationId) {
                iter.remove()
            }
        }
        dump()
    }

    fun saveZone(zone: Zone) {
        zones[zone.id] = zone
        dump()
    }

    fun hasOwnerPermission(zone: Zone, nation: Nation, playerId: UUID) =
        zone.nationId == nation.id && (zone.isOwner(playerId) || nation.isStaff(playerId))

    fun hasCoownerPermission(zone: Zone, nation: Nation, playerId: UUID) =
        zone.nationId == nation.id && (zone.isCoowner(playerId) || nation.isStaff(playerId))

    @OptIn(ExperimentalSerializationApi::class)
    override fun loadFromStream(stream: InputStream) {
        val jsonZones = Json.decodeFromStream<JsonZones>(stream)
        zones.clear()
        jsonZones.zones.forEach { z -> zones[z.id] = z }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun dumpToStream(stream: OutputStream) {
        Json.encodeToStream(JsonZones(zones.values.toList()), stream)
    }

    @Serializable
    private data class JsonZones(val zones: List<Zone> = emptyList())
}
