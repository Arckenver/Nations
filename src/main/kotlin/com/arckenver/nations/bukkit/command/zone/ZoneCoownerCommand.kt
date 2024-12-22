package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.command.Command

object ZoneCoownerCommand : Command("coowner") {
    init {
        withChild(ZoneCoownerAddCommand)
        withChild(ZoneCoownerRemoveCommand)
    }
}
