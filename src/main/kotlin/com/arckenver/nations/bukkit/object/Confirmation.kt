package com.arckenver.nations.bukkit.`object`

import com.arckenver.nations.bukkit.text.Text
import org.bukkit.entity.Player
import java.time.Instant
import java.util.*

class Confirmation(
    val player: Player,
    val operationDescription: Text,
    val executer: () -> Unit,
) {
    val id: UUID = UUID.randomUUID()
    val time: Instant = Instant.now()

    enum class Decision {
        APPROVE,
        CANCEL;
    }
}