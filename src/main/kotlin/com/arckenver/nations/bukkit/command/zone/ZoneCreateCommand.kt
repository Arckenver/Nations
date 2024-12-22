package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.`object`.Zone
import com.arckenver.nations.bukkit.text.Text

object ZoneCreateCommand : Command("create") {
    private val argName = CommandArgument.string("name")

    init {
        withDescription(Text.t("nations.cmd_desc_zone_create"))
        withArgument(argName)
    }

    override fun execute(ctx: CommandContext) {
        val name = ctx.argument(argName)

        val (_, nation) = ctx.senderPlayerNationStaff()
        val selection = ctx.senderSelection()

        if (ZoneManager.getZone(nation.id, name) != null) {
            throw CommandException.t("nations.zone_already_exists")
        }

        val zone = Zone(nation.id, name)

        if (!TerritoryManager.canClaimZone(selection, zone)) {
            throw CommandException.t("nations.cannot_claim_zone")
        }

        ZoneManager.createZone(zone)
        TerritoryManager.addTerritory(selection, Territory(zone))

        Nations.sendMessage(
            ctx.sender,
            Text.t("nations.zone_created", zone).green()
        )
    }
}
