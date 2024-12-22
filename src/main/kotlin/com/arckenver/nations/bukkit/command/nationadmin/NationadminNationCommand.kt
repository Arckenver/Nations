package com.arckenver.nations.bukkit.command.nationadmin

import com.arckenver.nations.bukkit.command.Command

object NationadminNationCommand : Command("nation") {
    init {
        withChild(NationadminNationDeleteCommand)
    }
}
