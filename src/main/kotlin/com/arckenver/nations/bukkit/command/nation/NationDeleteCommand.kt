package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.manager.ConfirmationManager
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.manager.ZoneManager
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.text.Text

object NationDeleteCommand : Command("delete") {
    init {
        withDescription(Text.t("nations.cmd_desc_nation_delete"))
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationPresident()

        ConfirmationManager.push(
            player,
            Text.t("nations.confirmation_operation_nation_delete", nation).yellow(),
        ) {
            for (zone in ZoneManager.listZones(nation.id)) {
                TerritoryManager.deleteTerritory(Territory(zone))
            }
            TerritoryManager.deleteTerritory(Territory(nation))

            ZoneManager.deleteZones(nation.id)
            NationManager.deleteNation(nation.id)

            for (player in Nations.plugin.server.onlinePlayers) {
                Nations.sendMessageIfOnline(player.uniqueId, Text.t("nations.nation_deleted", nation).yellow())
            }
        }
    }
}
