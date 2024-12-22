package com.arckenver.nations.bukkit.command.nationadmin

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.manager.ReserveManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.text.Text

object NationadminReserveDeleteCommand : Command("delete") {
    private val argReserve = CommandArgument.reserve("reserve")

    init {
        withDescription(Text.t("nations.cmd_desc_nationadmin_reserve_delete"))
        withArgument(argReserve)
    }

    override fun execute(ctx: CommandContext) {
        val reserve = ctx.argument(argReserve)

        TerritoryManager.deleteTerritory(Territory(reserve))
        ReserveManager.deleteReserve(reserve.id)

        Nations.sendMessage(ctx.sender, Text.t("nations.reserve_deleted", reserve).green())
    }
}
