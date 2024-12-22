package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.text.Text

object ZoneOwnerRemoveCommand : Command("remove") {
    init {
        withDescription(Text.t("nations.cmd_desc_zone_owner_remove"))
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationMember()
        val zone = ctx.senderZoneAtPlayerLocation()

        if (!ZoneManager.hasOwnerPermission(zone, nation, player.uniqueId)) {
            throw CommandException.t("nations.self_not_zone_owner")
        }

        val owner = zone.ownerId
        val coowners = zone.coowners.toList()

        zone.removeAllOwners()
        ZoneManager.saveZone(zone)

        owner?.let {
            Nations.sendMessageIfOnline(
                it,
                Text.t("nations.self_removed_owner", zone, Nations.playerName(player.uniqueId)).yellow()
            )
        }
        for (coowner in coowners) {
            Nations.sendMessageIfOnline(
                coowner,
                Text.t("nations.self_removed_coowner", zone, Nations.playerName(player.uniqueId)).yellow()
            )
        }

        Nations.sendMessage(
            ctx.sender,
            Text.t("nations.zone_owners_removed", zone).green()
        )
    }
}
