package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.text.Text

object NationLeaveCommand : Command("leave") {
    init {
        withDescription(Text.t("nations.cmd_desc_nation_leave"))
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationMember()

        if (nation.isPresident(player.uniqueId)) {
            throw CommandException.t("nations.cannot_leave_president")
        }

        nation.removeCitizen(player.uniqueId)
        NationManager.saveNation(nation)

        Nations.sendMessage(player, Text.t("nations.self_left_nation", nation.toText()).yellow())

        for (playerId in nation.citizens) {
            Nations.sendMessageIfOnline(
                playerId,
                Text.t("nations.player_left_nation", Nations.playerName(player.uniqueId)).yellow()
            )
        }
    }
}
