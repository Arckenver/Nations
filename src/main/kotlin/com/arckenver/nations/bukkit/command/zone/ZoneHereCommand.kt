package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.text.Text

object ZoneHereCommand : Command("here") {
    init {
        withDescription(Text.t("nations.cmd_desc_zone_here"))
    }

    override fun execute(ctx: CommandContext) {
        val zone = ctx.senderZoneAtPlayerLocation()

        Nations.sendMessage(ctx.sender, ZoneInfoCommand.infoText(zone))
    }
}
