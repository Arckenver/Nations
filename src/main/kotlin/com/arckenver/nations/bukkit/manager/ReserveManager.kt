package com.arckenver.nations.bukkit.manager

import com.arckenver.nations.bukkit.`object`.Reserve
import com.arckenver.nations.bukkit.serialization.Json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import kotlin.collections.forEach

object ReserveManager : PersistentManager("reserves.json", defaultFileContent = "{}") {
    private val reserves: MutableMap<UUID, Reserve> = mutableMapOf()

    fun getReserve(id: UUID): Reserve? {
        return reserves[id]
    }

    fun getReserve(name: String): Reserve? {
        return reserves.values.find { it.name.equals(name, ignoreCase = true) }
    }

    fun listReserves(): Iterable<Reserve> {
        return reserves.values
    }

    fun createReserve(reserve: Reserve) {
        if (reserves.containsKey(reserve.id)) {
            throw IllegalArgumentException("Reserve ${reserve.id} already exists")
        }

        reserves[reserve.id] = reserve
        dump()
    }

    fun getOrCreateReserve(name: String): Reserve {
        val existing = getReserve(name)
        if (existing != null) {
            return existing
        }
        val created = Reserve(name)
        createReserve(created)
        return created
    }

    fun deleteReserve(id: UUID) {
        reserves.remove(id)?.let { dump() }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun loadFromStream(stream: InputStream) {
        val jsonReserves = Json.decodeFromStream<JsonReserves>(stream)
        reserves.clear()
        jsonReserves.reserves.forEach { r -> reserves[r.id] = r }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun dumpToStream(stream: OutputStream) {
        Json.encodeToStream(JsonReserves(reserves.values.toList()), stream)
    }

    @Serializable
    private data class JsonReserves(val reserves: List<Reserve> = emptyList())
}
