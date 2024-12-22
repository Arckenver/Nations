package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.`object`.Balance
import com.arckenver.nations.bukkit.text.Text

object ZoneSellCommand : Command("sell") {
    private val argAmount = CommandArgument.balance("amount")

    init {
        withDescription(Text.t("nations.cmd_desc_zone_sell"))
        withArgument(argAmount)
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationMember()
        val zone = ctx.senderZoneAtPlayerLocation()

        if (!ZoneManager.hasOwnerPermission(zone, nation, player.uniqueId)) {
            throw CommandException.t("nations.self_not_zone_owner")
        }

        val amount = ctx.argument(argAmount)
        if (amount < Balance.ZERO) {
            throw CommandException.t("nations.amount_must_be_positive_or_zero")
        }

        zone.price = amount
        ZoneManager.saveZone(zone)

        for (citizen in nation.citizens) {
            Nations.sendMessageIfOnline(
                citizen,
                Text.t("nations.zone_put_for_sale", zone, amount).yellow()
            )
        }
    }
}