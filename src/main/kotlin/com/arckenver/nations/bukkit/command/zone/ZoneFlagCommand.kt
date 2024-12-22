package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.`object`.Flag
import com.arckenver.nations.bukkit.`object`.Nation
import com.arckenver.nations.bukkit.`object`.Zone
import com.arckenver.nations.bukkit.text.Text

object ZoneFlagCommand : Command("flag") {
    private val argFlag = CommandArgument.enum<Flag>(optional = true)
    private val argValue = CommandArgument.boolean(optional = true)

    init {
        withDescription(Text.t("nations.cmd_desc_zone_flag"))
        withArgument(argFlag)
        withArgument(argValue)
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationMember()
        val zone = ctx.senderZoneAtPlayerLocation()

        if (!ZoneManager.hasCoownerPermission(zone, nation, player.uniqueId)) {
            throw CommandException.t("nations.self_not_zone_coowner")
        }

        val flag = ctx.optionalArgument(argFlag)
        if (flag == null) {
            Nations.sendMessage(ctx.sender, flagsText(nation, zone))
            return
        }

        val value = ctx.optionalArgument(argValue) ?: throw CommandException.t(
            "nations.missing_argument",
            Text(argValue.name)
        )

        zone.flags[flag] = value
        ZoneManager.saveZone(zone)

        Nations.sendMessage(ctx.sender, flagsText(nation, zone))
    }

    private fun flagsText(nation: Nation, zone: Zone) = Text.build {
        +Text.header(Text.build {
            +Text.nationZone(nation, zone)
            +Text(" - ").gray()
            +Text.t("nations.flags").yellow()
        })

        for ((flag, value) in zone.flags) {
            +"\n"
            +flag.toText().gold()
            +Text(": ").gold()
            +Text.yesNo(value)
                .bold()
                .runCommand("/zone flag ${flag.name.lowercase()} ${!value}")
            +Text(" <- ").darkGray().italic()
            +Text.t("nations.click_here").darkGray().italic()
        }
    }
}
