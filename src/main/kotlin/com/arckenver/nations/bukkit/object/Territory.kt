package com.arckenver.nations.bukkit.`object`

import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.ReserveManager
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.text.Textable
import com.arckenver.nations.bukkit.text.UnknownText
import java.util.UUID

data class Territory(val kind: Kind, val id: UUID) : Textable {
    constructor(reserve: Reserve) : this(Kind.RESERVE, reserve.id)
    constructor(nation: Nation) : this(Kind.NATION, nation.id)
    constructor(zone: Zone) : this(Kind.ZONE, zone.id)

    enum class Kind(val precedence: Int) {
        RESERVE(2),
        NATION(0),
        ZONE(1);
    }

    override fun toText(clickable: Boolean) = when (kind) {
        Kind.RESERVE -> ReserveManager.getReserve(id)?.toText(clickable)
        Kind.NATION -> NationManager.getNation(id)?.toText(clickable)
        Kind.ZONE -> ZoneManager.getZone(id)?.toText(clickable)
    } ?: UnknownText

    operator fun compareTo(t: Territory): Int {
        return kind.precedence.compareTo(t.kind.precedence)
    }
}
