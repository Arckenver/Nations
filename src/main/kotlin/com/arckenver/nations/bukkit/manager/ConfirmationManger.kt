package com.arckenver.nations.bukkit.manager

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.`object`.Confirmation
import com.arckenver.nations.bukkit.text.Text
import java.time.Instant
import java.util.UUID
import org.bukkit.entity.Player

private const val CONFIRMATION_EXPIRATION_SECONDS: Long = 3 * 60

object ConfirmationManager : TaskTimerAsyncManager(100, 100) {
    private val pendingConfirmations = mutableMapOf<UUID, Confirmation>()

    fun push(
        player: Player,
        operationDescription: Text,
        executer: () -> Unit,
    ) {
        val confirmation = Confirmation(player, operationDescription, executer)
        pendingConfirmations[confirmation.id] = confirmation

        Nations.sendMessage(confirmation.player, Text.build {
            +Text.t("nations.confirmation_title").gold()
            +"\n"
            +confirmation.operationDescription
            +"\n\n    "
            +Text.t("nations.confirmation_approve")
                .yellow()
                .bold()
                .runCommand("/nation confirm ${Confirmation.Decision.APPROVE.name.lowercase()} ${confirmation.id}")
            +"    "
            +Text.t("nations.confirmation_cancel")
                .yellow()
                .bold()
                .runCommand("/nation confirm ${Confirmation.Decision.CANCEL.name.lowercase()} ${confirmation.id}")
            +"\n"
        })
    }

    @Throws(CommandException::class)
    fun approve(confirmationId: UUID, playerId: UUID) {
        val confirmation = getPlayerConfirmation(confirmationId, playerId)
            ?: throw CommandException.t("nations.no_such_confirmation")

        pendingConfirmations.remove(confirmation.id)
        return confirmation.executer()
    }

    @Throws(CommandException::class)
    fun cancel(confirmationId: UUID, playerId: UUID) {
        val confirmation = getPlayerConfirmation(confirmationId, playerId)
            ?: throw CommandException.t("nations.no_such_confirmation")

        pendingConfirmations.remove(confirmation.id)
        Nations.sendMessage(confirmation.player, Text.t("nations.confirmation_cancelled").green())
    }

    private fun getPlayerConfirmation(confirmationId: UUID, playerId: UUID): Confirmation? {
        val confirmation = pendingConfirmations[confirmationId] ?: return null
        return if (confirmation.player.uniqueId == playerId) confirmation else null
    }

    override fun runTask() {
        val now = Instant.now()
        for (confirmation in pendingConfirmations.values) {
            if (confirmation.time.plusSeconds(CONFIRMATION_EXPIRATION_SECONDS).isBefore(now)) {
                pendingConfirmations.remove(confirmation.id)
            }
        }
    }
}
