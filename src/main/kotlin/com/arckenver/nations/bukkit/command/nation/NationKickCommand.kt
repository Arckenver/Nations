package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.text.Text

object NationKickCommand : Command("kick") {
    private val argPlayer = CommandArgument.compatriot("player")

    init {
        withDescription(Text.t("nations.cmd_desc_nation_kick"))
        withArgument(argPlayer)
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationStaff()

        val target = ctx.argument(argPlayer)
        if (target.uniqueId == player.uniqueId) {
            throw CommandException.t("nations.cannot_kick_self")
        }

        if (!nation.isCitizen(target.uniqueId)) {
            throw CommandException.t("nations.player_not_in_nation", Nations.playerName(target.uniqueId))
        }

        nation.removeCitizen(target.uniqueId)
        NationManager.saveNation(nation)

        Nations.sendMessage(target, Text.t("nations.self_kicked_from_nation", nation.toText()).yellow())

        for (playerId in nation.citizens) {
            Nations.sendMessageIfOnline(
                playerId,
                Text.t("nations.player_kicked_from_nation", Nations.playerName(target.uniqueId)).yellow()
            )
        }
    }
}