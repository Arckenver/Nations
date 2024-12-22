package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.ConfirmationManager
import com.arckenver.nations.bukkit.manager.SelectionManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.text.Text
import com.arckenver.nations.bukkit.text.UnknownText

object NationUnclaimCommand : Command("unclaim") {
    init {
        withDescription(Text.t("nations.cmd_desc_nation_unclaim"))
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationStaff()
        val selection = ctx.senderSelection()

        if (!TerritoryManager.isIntersecting(selection, Territory(nation))) {
            throw CommandException.t("nations.must_select_nation_intersecting")
        }
        val zoneId = TerritoryManager.territoriesIntersecting(selection)
            .filter { it.kind == Territory.Kind.ZONE }
            .map { it.id }
            .firstOrNull()
        if (zoneId != null) {
            throw CommandException.t(
                "nations.cannot_unclaim_nation_because_zone",
                ZoneManager.getZone(zoneId) ?: UnknownText
            )
        }

        val unclaimArea = TerritoryManager.overlapArea(selection, Territory(nation))
        if (unclaimArea >= TerritoryManager.areaOf(Territory(nation))) {
            throw CommandException.t("nations.cannot_unclaim_whole_nation")
        }

        ConfirmationManager.push(
            player,
            Text.t("nations.confirmation_operation_nation_unclaim", Text(unclaimArea), nation).yellow(),
        ) {
            TerritoryManager.cutTerritory(selection, Territory(nation))

            SelectionManager.clearSelection(player.uniqueId)

            Nations.sendMessage(ctx.sender, Text.t("nations.unclaimed_area").green())
        }
    }
}
