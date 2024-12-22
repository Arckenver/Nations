package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.text.Text

object ZoneCoownerRemoveCommand : Command("remove") {
    val argPlayer = CommandArgument.compatriot("player")

    init {
        withDescription(Text.t("nations.cmd_desc_zone_coowner_remove"))
        withArgument(argPlayer)
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationMember()
        val zone = ctx.senderZoneAtPlayerLocation()

        if (!ZoneManager.hasCoownerPermission(zone, nation, player.uniqueId)) {
            throw CommandException.t("nations.self_not_zone_coowner")
        }

        val target = ctx.argument(argPlayer)

        if (!zone.isCoowner(target.uniqueId)) {
            throw CommandException.t("nations.player_not_coowner", Nations.playerName(target.uniqueId), zone)
        }

        zone.removeCoowner(target.uniqueId)
        ZoneManager.saveZone(zone)

        Nations.sendMessageIfOnline(
            target.uniqueId,
            Text.t("nations.self_removed_coowner", zone, Nations.playerName(player.uniqueId)).yellow()
        )
        Nations.sendMessage(
            ctx.sender,
            Text.t("nations.zone_coowner_removed", Nations.playerName(target.uniqueId), zone).green()
        )
    }
}
