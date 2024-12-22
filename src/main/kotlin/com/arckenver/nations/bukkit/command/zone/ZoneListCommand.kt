package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.text.Text

object ZoneListCommand : Command("list") {
    init {
        withDescription(Text.t("nations.cmd_desc_zone_list"))
    }

    override fun execute(ctx: CommandContext) {
        val (_, nation) = ctx.senderPlayerNationMember()

        val zones = ZoneManager.listZones(nation.id).toList()
        if (zones.isEmpty()) {
            Nations.sendMessage(
                ctx.sender,
                Text.t("nations.no_zones_in_nation").yellow()
            )
            return
        }

        Nations.sendMessage(ctx.sender, Text.build {
            +Text.header(Text.build {
                +nation
                +Text(" - ").gray()
                +Text.t("nations.zones").yellow()
            })
            +"\n"
            Text.list(zones.map { it.toText(clickable = true) })
        })
    }
}
