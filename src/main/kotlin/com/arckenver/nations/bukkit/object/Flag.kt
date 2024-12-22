package com.arckenver.nations.bukkit.`object`

import com.arckenver.nations.bukkit.text.Text
import com.arckenver.nations.bukkit.text.Textable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Flag : Textable {
    @SerialName("pvp")
    PVP,

    @SerialName("fire")
    FIRE,

    @SerialName("mobs")
    MOBS,

    @SerialName("explosions")
    EXPLOSIONS;

    override fun toText(clickable: Boolean) = when (this) {
        PVP -> Text.t("nations.flags_pvp")
        FIRE -> Text.t("nations.flags_fire")
        MOBS -> Text.t("nations.flags_mobs")
        EXPLOSIONS -> Text.t("nations.flags_explosions")
    }
}

val worldFlags = listOf(
    Flag.PVP,
    Flag.FIRE,
    Flag.MOBS,
    Flag.EXPLOSIONS,
)

val defaultWorldFlags = worldFlags.associateWith { true }

val nationFlags = listOf(
    Flag.PVP,
    Flag.FIRE,
    Flag.MOBS,
    Flag.EXPLOSIONS,
)

val defaultNationFlags = nationFlags.associateWith { false }

val zoneFlags = listOf(
    Flag.PVP,
    Flag.FIRE,
    Flag.MOBS,
    Flag.EXPLOSIONS,
)

val defaultZoneFlags = zoneFlags.associateWith { false }

fun cleanFlags(flags: MutableMap<Flag, Boolean>, defaultFlags: Map<Flag, Boolean>) {
    for ((flag, value) in defaultFlags) {
        flags.putIfAbsent(flag, value)
    }
    for (flag in flags.keys) {
        if (!defaultFlags.containsKey(flag)) {
            flags.remove(flag)
        }
    }
}
