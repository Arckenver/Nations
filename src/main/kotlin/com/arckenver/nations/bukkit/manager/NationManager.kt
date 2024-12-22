package com.arckenver.nations.bukkit.manager

import com.arckenver.nations.bukkit.`object`.Nation
import com.arckenver.nations.bukkit.serialization.Json
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

object NationManager : PersistentManager("nations.json", defaultFileContent = "{}") {
    private val nations: MutableMap<UUID, Nation> = mutableMapOf()

    fun getNation(id: UUID): Nation? {
        return nations[id]
    }

    fun getNation(name: String) = nations.values.find { it.name.equals(name, ignoreCase = true) }

    fun getPlayerNation(playerId: UUID) = nations.values.find { it.isCitizen(playerId) }

    fun listNations(): Iterable<Nation> {
        return nations.values
    }

    fun saveNation(nation: Nation) {
        nations[nation.id] = nation
        dump()
    }

    fun deleteNation(id: UUID) {
        nations.remove(id)?.let { dump() }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun loadFromStream(stream: InputStream) {
        val jsonNations = Json.decodeFromStream<JsonNations>(stream)
        nations.clear()
        jsonNations.nations.forEach { n -> nations[n.id] = n }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun dumpToStream(stream: OutputStream) {
        Json.encodeToStream(JsonNations(nations.values.toList()), stream)
    }

    @Serializable
    private data class JsonNations(val nations: List<Nation> = emptyList())
}
