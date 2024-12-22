package com.arckenver.nations.bukkit.`object`

import java.time.Instant
import java.util.UUID

data class Invite(
    val kind: Kind,
    val nationId: UUID,
    val playerId: UUID,
) {
    val time: Instant = Instant.now()

    enum class Kind {
        INVITATION,
        REQUEST;

        fun opposite() = when (this) {
            INVITATION -> REQUEST
            REQUEST -> INVITATION
        }
    }

    enum class Result {
        ACCEPTED,
        SENT,
        ALREADY_SENT
    }
}
