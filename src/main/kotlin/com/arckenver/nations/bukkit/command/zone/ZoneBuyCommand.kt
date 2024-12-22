package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.EconomyManager
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.text.Text

object ZoneBuyCommand : Command("buy") {
    init {
        withDescription(Text.t("nations.cmd_desc_zone_buy"))
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationMember()
        val zone = ctx.senderZoneAtPlayerLocation()

        if (zone.nationId != nation.id) {
            val zoneNation = NationManager.getNation(zone.nationId)!!
            throw CommandException.t("nations.must_be_citizen_of_nation", zoneNation)
        }

        val price = zone.price ?: throw CommandException.t("nations.zone_not_for_sale", zone)

        if (zone.isOwner(player.uniqueId)) {
            zone.price = null
            ZoneManager.saveZone(zone)

            Nations.sendMessage(ctx.sender, Text.t("nations.zone_no_longer_for_sale", zone).green())
            return
        }

        if (EconomyManager.balance(player.uniqueId) < price) {
            throw CommandException.t("nations.player_insufficient_funds", price)
        }

        EconomyManager.withdraw(player.uniqueId, price)
        val previousOwnerId = zone.ownerId
        if (previousOwnerId != null) {
            EconomyManager.deposit(previousOwnerId, price)
        } else {
            nation.balance += price
            NationManager.saveNation(nation)
        }

        zone.removeAllOwners()
        zone.ownerId = player.uniqueId
        zone.price = null
        ZoneManager.saveZone(zone)

        if (previousOwnerId != null) {
            Nations.sendMessageIfOnline(
                previousOwnerId,
                Text.t(
                    "nations.zone_purchased",
                    Nations.playerName(player.uniqueId),
                    zone,
                    price,
                ).yellow()
            )
        }

        Nations.sendMessage(
            ctx.sender,
            Text.t("nations.self_purchased_zone", zone, price).green()
        )
    }
}