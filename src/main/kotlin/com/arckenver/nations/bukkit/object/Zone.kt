package com.arckenver.nations.bukkit.`object`

import com.arckenver.nations.bukkit.text.Text
import com.arckenver.nations.bukkit.text.Textable
import java.util.UUID
import kotlinx.serialization.Contextual
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Zone(
    val id: @Contextual UUID,
    val nationId: @Contextual UUID,
    var name: String,
    var ownerId: @Contextual UUID? = null,
    val coowners: MutableList<@Contextual UUID> = mutableListOf(),
    var price: Balance? = null,
    @EncodeDefault
    val permissions: MutableMap<Actor, MutableMap<Action, Boolean>> =
        defaultZonePermissions
            .mapValues { it.value.toMutableMap() }
            .toMutableMap(),
    @EncodeDefault
    val flags: MutableMap<Flag, Boolean> = defaultZoneFlags.toMutableMap(),
) : Textable {
    constructor(nationId: UUID, name: String) : this(UUID.randomUUID(), nationId, name)

    init {
        cleanFlags(flags, defaultZoneFlags)
        cleanPermissions(permissions, defaultZonePermissions)
    }

    fun isOwner(playerId: UUID) = ownerId == playerId

    fun setOwner(playerId: UUID) {
        coowners.remove(playerId)
        ownerId = playerId
    }

    fun removeAllOwners() {
        ownerId = null
        coowners.clear()
    }

    fun isCoowner(playerId: UUID) = isOwner(playerId) || coowners.contains(playerId)

    fun addCoowner(playerId: UUID) {
        if (!coowners.contains(playerId)) {
            coowners.add(playerId)
        }
    }

    fun removeCoowner(playerId: UUID) {
        coowners.remove(playerId)
    }

    fun getPermission(actor: Actor, action: Action) = permissions[actor]?.get(action) == true

    fun getFlag(flag: Flag) = flags[flag] == true

    override fun toText(clickable: Boolean): Text {
        var t = Text(name).darkPurple()
        if (clickable) {
            t = t.runCommand("/zone info $name")
        }
        return t
    }
}
