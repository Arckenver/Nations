package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.command.Command

object NationMinisterCommand : Command("minister") {
    init {
        withChild(NationMinisterAddCommand)
        withChild(NationMinisterRemoveCommand)
    }
}
