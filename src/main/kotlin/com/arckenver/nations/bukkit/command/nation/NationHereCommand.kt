package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.text.Text

object NationHereCommand : Command("here") {
    init {
        withDescription(Text.t("nations.cmd_desc_nation_here"))
    }

    override fun execute(ctx: CommandContext) {
        val player = ctx.senderPlayer()

        val territory = TerritoryManager.territoryAt(
            Nations.locationPoint(player.location),
            Territory.Kind.NATION
        ) ?: throw CommandException.t("nations.no_nation_here")

        val nation = NationManager.getNation(territory.id)!!

        Nations.sendMessage(ctx.sender, NationInfoCommand.infoText(nation))
    }
}
