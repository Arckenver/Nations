package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.`object`.Action
import com.arckenver.nations.bukkit.`object`.Actor
import com.arckenver.nations.bukkit.`object`.Nation
import com.arckenver.nations.bukkit.`object`.Zone
import com.arckenver.nations.bukkit.`object`.zoneActions
import com.arckenver.nations.bukkit.`object`.zoneActors
import com.arckenver.nations.bukkit.text.Text

object ZonePermCommand : Command("perm") {
    private val argActor = CommandArgument.enum<Actor>(
        optional = true,
        entries = zoneActors.toTypedArray(),
    )
    private val argAction = CommandArgument.enum<Action>(
        optional = true,
        entries = zoneActions.toTypedArray(),
    )
    private val argValue = CommandArgument.boolean(optional = true)

    init {
        withDescription(Text.t("nations.cmd_desc_zone_perm"))
        withArgument(argActor)
        withArgument(argAction)
        withArgument(argValue)
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationMember()
        val zone = ctx.senderZoneAtPlayerLocation()

        if (!ZoneManager.hasCoownerPermission(zone, nation, player.uniqueId)) {
            throw CommandException.t("nations.self_not_zone_coowner")
        }

        val actor = ctx.optionalArgument(argActor)
        if (actor == null) {
            Nations.sendMessage(ctx.sender, permissionsText(nation, zone))
            return
        }

        val action = ctx.optionalArgument(argAction) ?: throw CommandException.t(
            "nations.missing_argument",
            Text(argAction.name)
        )

        val value = ctx.optionalArgument(argValue) ?: throw CommandException.t(
            "nations.missing_argument",
            Text(argValue.name)
        )

        zone.permissions[actor]!![action] = value
        ZoneManager.saveZone(zone)

        Nations.sendMessage(ctx.sender, permissionsText(nation, zone))
    }

    private fun permissionsText(nation: Nation, zone: Zone) = Text.build {
        +Text.header(Text.build {
            +Text.nationZone(nation, zone)
            +Text(" - ").gray()
            +Text.t("nations.permissions").yellow()
        })

        for ((actor, actions) in zone.permissions) {
            +"\n"
            +actor.toText().gold()
            +Text(":").gold()

            for ((action, value) in actions) {
                +"\n    "
                +action.toText().gold()
                +Text(": ").gold()
                +Text.yesNo(value)
                    .bold()
                    .runCommand("/zone perm ${actor.name.lowercase()} ${action.name.lowercase()} ${!value}")
            }
        }
    }
}
