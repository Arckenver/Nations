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
data class World(
    val id: @Contextual UUID,
    var name: String,
    @EncodeDefault
    val permissions: MutableMap<Action, Boolean> = defaultWorldPermissions.toMutableMap(),
    @EncodeDefault
    val flags: MutableMap<Flag, Boolean> = defaultWorldFlags.toMutableMap(),
) : Textable {
    init {
        cleanFlags(flags, defaultWorldFlags)
        cleanActions(permissions, defaultWorldPermissions)
    }

    fun getPermission(action: Action) = permissions[action] == true

    fun getFlag(flag: Flag) = flags[flag] == true

    override fun toText(clickable: Boolean): Text {
        var t = Text(name).darkGreen()
        if (clickable) {
            t = t.runCommand("/nationadmin world info $name")
        }
        return t
    }
}
