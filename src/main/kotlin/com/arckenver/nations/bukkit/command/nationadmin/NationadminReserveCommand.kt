package com.arckenver.nations.bukkit.command.nationadmin

import com.arckenver.nations.bukkit.command.Command

object NationadminReserveCommand : Command("reserve") {
    init {
        withChild(NationadminReserveClaimCommand)
        withChild(NationadminReserveUnclaimCommand)
        withChild(NationadminReserveDeleteCommand)
    }
}
