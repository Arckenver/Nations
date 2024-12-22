package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.text.Text

object ZoneDeleteCommand : Command("delete") {
    init {
        withDescription(Text.t("nations.cmd_desc_zone_delete"))
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationMember()
        val zone = ctx.senderZoneAtPlayerLocation()

        if (!ZoneManager.hasOwnerPermission(zone, nation, player.uniqueId)) {
            throw CommandException.t("nations.self_not_zone_owner")
        }

        TerritoryManager.deleteTerritory(Territory(zone))
        ZoneManager.deleteZone(zone.id)

        Nations.sendMessage(
            ctx.sender,
            Text.t("nations.zone_deleted", zone).green()
        )
    }
}