package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.command.Command

object ZoneOwnerCommand : Command("owner") {
    init {
        withChild(ZoneOwnerSetCommand)
        withChild(ZoneOwnerRemoveCommand)
    }
}
