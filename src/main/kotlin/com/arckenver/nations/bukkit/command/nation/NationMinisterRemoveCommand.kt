package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.text.Text

object NationMinisterRemoveCommand : Command("remove") {
    private val argPlayer = CommandArgument.compatriot("player")

    init {
        withDescription(Text.t("nations.cmd_desc_nation_minister_remove"))
        withArgument(argPlayer)
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationStaff()

        val target = ctx.argument(argPlayer)
        if (!nation.isMinister(target.uniqueId)) {
            throw CommandException.t("nations.player_not_minister")
        }

        nation.removeMinister(target.uniqueId)
        NationManager.saveNation(nation)

        Nations.sendMessage(
            target,
            Text.t("nations.self_demoted_minister").yellow()
        )
        Nations.sendMessage(
            player,
            Text.t("nations.player_demoted_minister", Nations.playerName(target.uniqueId)).green()
        )
    }
}
