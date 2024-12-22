package com.arckenver.nations.bukkit.manager

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.`object`.World
import com.arckenver.nations.bukkit.serialization.Json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

object WorldManager : PersistentManager("worlds.json", defaultFileContent = "{}") {
    private val worlds: MutableMap<UUID, World> = mutableMapOf()

    fun listWorlds(): Iterable<World> {
        val bukkitWorlds = Nations.plugin.server.worlds.toList()
        if (worlds.size != bukkitWorlds.size || bukkitWorlds.any { w -> !worlds.containsKey(w.uid) }) {
            for (bukkitWorld in bukkitWorlds) {
                val world = worlds[bukkitWorld.uid]
                if (world == null) {
                    worlds[bukkitWorld.uid] = World(bukkitWorld.uid, bukkitWorld.name)
                } else if (world.name != bukkitWorld.name) {
                    world.name = bukkitWorld.name
                }
            }
            for (world in worlds.values.toList()) {
                if (bukkitWorlds.none { w -> w.uid == world.id }) {
                    worlds.remove(world.id)
                }
            }
            dump()
        }
        return worlds.values
    }

    fun getWorld(id: UUID): World? {
        val world = worlds[id]
        val bukkitWorld = Nations.plugin.server.getWorld(id)

        if (world == null) {
            if (bukkitWorld == null) {
                return null
            }

            val createdWorld = World(id, bukkitWorld.name)
            worlds[id] = createdWorld
            dump()
            return createdWorld
        }

        if (bukkitWorld == null) {
            worlds.remove(id)
            dump()
            return null
        }

        if (world.name != bukkitWorld.name) {
            world.name = bukkitWorld.name
            dump()
        }

        return world
    }

    fun getWorld(name: String): World? {
        val bukkitWorld = Nations.plugin.server.getWorld(name) ?: return null
        return getWorld(bukkitWorld.uid)
    }

    fun saveWorld(world: World) {
        worlds[world.id] = world
        dump()
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun loadFromStream(stream: InputStream) {
        val jsonWorlds = Json.decodeFromStream<JsonWorlds>(stream)
        worlds.clear()
        jsonWorlds.worlds.forEach { w -> worlds[w.id] = w }

        listWorlds()
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun dumpToStream(stream: OutputStream) {
        Json.encodeToStream(JsonWorlds(worlds.values.toList()), stream)
    }

    @Serializable
    private data class JsonWorlds(val worlds: List<World> = emptyList())
}
