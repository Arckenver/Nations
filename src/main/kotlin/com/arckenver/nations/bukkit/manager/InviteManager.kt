package com.arckenver.nations.bukkit.manager

import com.arckenver.nations.bukkit.`object`.Invite
import java.time.Instant
import java.util.UUID

private const val INVITE_EXPIRATION_SECONDS: Long = 3 * 60

object InviteManager : TaskTimerAsyncManager(100, 100) {
    private val invites: MutableList<Invite> = mutableListOf()

    fun send(kind: Invite.Kind, nationId: UUID, playerId: UUID): Invite.Result {
        val request = getInvite(kind.opposite(), nationId, playerId)
        if (request != null) {
            invites.remove(request)
            return Invite.Result.ACCEPTED
        }

        val invitation = getInvite(kind, nationId, playerId)
        if (invitation != null) {
            return Invite.Result.ALREADY_SENT
        }

        invites.add(Invite(kind, nationId, playerId))
        return Invite.Result.SENT
    }

    fun delete(nationId: UUID, playerId: UUID) {
        invites.removeAll { it.nationId == nationId && it.playerId == playerId }
    }

    private fun getInvite(kind: Invite.Kind, nationId: UUID, playerId: UUID) =
        invites.find { it.kind == kind && it.nationId == nationId && it.playerId == playerId }

    override fun runTask() {
        val now = Instant.now()
        for (invite in invites) {
            if (invite.time.plusSeconds(INVITE_EXPIRATION_SECONDS).isBefore(now)) {
                invites.remove(invite)
            }
        }
    }
}
