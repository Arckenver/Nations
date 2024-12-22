package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.text.Text

object ZoneOwnerSetCommand : Command("set") {
    val argPlayer = CommandArgument.compatriot("player")

    init {
        withDescription(Text.t("nations.cmd_desc_zone_owner_set"))
        withArgument(argPlayer)
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationMember()
        val zone = ctx.senderZoneAtPlayerLocation()

        if (!ZoneManager.hasOwnerPermission(zone, nation, player.uniqueId)) {
            throw CommandException.t("nations.self_not_zone_owner")
        }

        val target = ctx.argument(argPlayer)

        if (!nation.isCitizen(target.uniqueId)) {
            throw CommandException.t("nations.player_not_in_nation", Nations.playerName(target.uniqueId))
        }

        zone.setOwner(target.uniqueId)
        ZoneManager.saveZone(zone)

        Nations.sendMessageIfOnline(
            target.uniqueId,
            Text.t("nations.self_set_zone_owner", zone, Nations.playerName(player.uniqueId)).yellow()
        )

        Nations.sendMessage(
            ctx.sender,
            Text.t("nations.zone_owner_set", Nations.playerName(target.uniqueId), zone).green()
        )
    }
}
