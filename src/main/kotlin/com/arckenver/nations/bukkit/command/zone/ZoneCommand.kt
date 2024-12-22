package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.command.Command

object ZoneCommand : Command("zone") {
    init {
        withAliases("z")
        withChild(ZoneInfoCommand)
        withChild(ZoneHereCommand)
        withChild(ZoneListCommand)
        withChild(ZoneCreateCommand)
        withChild(ZoneOwnerCommand)
        withChild(ZoneCoownerCommand)
        withChild(ZoneRenameCommand)
        withChild(ZoneSellCommand)
        withChild(ZoneBuyCommand)
        withChild(ZonePermCommand)
        withChild(ZoneFlagCommand)
        withChild(ZoneDeleteCommand)
    }
}
