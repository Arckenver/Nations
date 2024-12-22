package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.text.Text

object ZoneRenameCommand : Command("rename") {
    private val argName = CommandArgument.string("name")

    init {
        withDescription(Text.t("nations.cmd_desc_zone_rename"))
        withArgument(argName)
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationMember()
        val zone = ctx.senderZoneAtPlayerLocation()

        if (!ZoneManager.hasCoownerPermission(zone, nation, player.uniqueId)) {
            throw CommandException.t("nations.self_not_zone_coowner")
        }

        val previousName = zone.name

        val name = ctx.argument(argName)

        if (ZoneManager.getZone(nation.id, name) != null) {
            throw CommandException.t("nations.zone_already_exists")
        }

        zone.name = name
        ZoneManager.saveZone(zone)

        Nations.sendMessage(ctx.sender, Text.t("nations.zone_renamed", Text(previousName), Text(name)))
    }
}
