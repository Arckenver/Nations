package com.arckenver.nations.bukkit.command.nationadmin

import com.arckenver.nations.bukkit.command.Command

object NationadminCommand : Command("nationadmin") {
    init {
        withAliases("na")
        withChild(NationadminNationCommand)
        withChild(NationadminReserveCommand)
        withChild(NationadminWorldCommand)
    }
}
