package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.text.Text

object NationResignCommand : Command("resign") {
    private val argSuccessor = CommandArgument.compatriot("successor")

    init {
        withDescription(Text.t("nations.cmd_desc_nation_resign"))
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationPresident()

        val successor = ctx.argument(argSuccessor)
        if (!nation.isCitizen(successor.uniqueId)) {
            throw CommandException.t("nations.must_choose_citizen")
        }

        nation.setPresident(successor.uniqueId)
        NationManager.saveNation(nation)

        val msg = Text.t(
            "nations.president_resigned",
            Nations.playerName(player.uniqueId),
            nation.toText(),
            Nations.playerName(successor.uniqueId)
        ).yellow()

        Nations.sendMessage(player, msg)

        for (playerId in nation.citizens) {
            Nations.sendMessageIfOnline(playerId, msg)
        }
    }
}
