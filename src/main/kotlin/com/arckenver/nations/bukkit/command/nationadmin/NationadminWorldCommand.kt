package com.arckenver.nations.bukkit.command.nationadmin

import com.arckenver.nations.bukkit.command.Command

object NationadminWorldCommand : Command("world") {
    init {
        withChild(NationadminWorldListCommand)
        withChild(NationadminWorldInfoCommand)
        withChild(NationadminWorldPermCommand)
        withChild(NationadminWorldFlagCommand)
    }
}
