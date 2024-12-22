package com.arckenver.nations.bukkit.command.nationadmin

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.text.Text

object NationadminReserveUnclaimCommand : Command("unclaim") {
    init {
        withDescription(Text.t("nations.cmd_desc_nationadmin_reserve_unclaim"))
    }

    override fun execute(ctx: CommandContext) {
        val selection = ctx.senderSelection()

        TerritoryManager.cutTerritory(selection) { it.kind == Territory.Kind.RESERVE }

        Nations.sendMessage(ctx.sender, Text.t("nations.unclaimed_reserve_area").green())
    }
}
