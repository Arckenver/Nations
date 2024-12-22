package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.command.Command

object NationCommand : Command("nation") {
    init {
        withAliases("n")
        withChild(NationListCommand)
        withChild(NationInfoCommand)
        withChild(NationHereCommand)
        withChild(NationJoinCommand)
        withChild(NationCreateCommand)
        withChild(NationDeleteCommand)
        withChild(NationRenameCommand)
        withChild(NationDepositCommand)
        withChild(NationWithdrawCommand)
        withChild(NationInviteCommand)
        withChild(NationClaimCommand)
        withChild(NationUnclaimCommand)
        withChild(NationConfirmCommand)
        withChild(NationKickCommand)
        withChild(NationLeaveCommand)
        withChild(NationResignCommand)
        withChild(NationMinisterCommand)
        withChild(NationPermCommand)
        withChild(NationFlagCommand)
    }
}
