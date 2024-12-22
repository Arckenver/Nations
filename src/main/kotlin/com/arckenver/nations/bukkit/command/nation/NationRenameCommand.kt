package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.text.Text

object NationRenameCommand : Command("rename") {
    private val argName = CommandArgument.string("name")

    init {
        withDescription(Text.t("nations.cmd_desc_nation_rename"))
        withArgument(argName)
    }

    override fun execute(ctx: CommandContext) {
        val (_, nation) = ctx.senderPlayerNationStaff()

        val name = ctx.argument(argName)

        if (NationManager.getNation(name) != null) {
            throw CommandException.t("nations.nation_already_exists")
        }

        nation.name = name
        NationManager.saveNation(nation)

        for (citizen in nation.citizens) {
            Nations.sendMessageIfOnline(citizen, Text.t("nations.nation_renamed", nation).yellow())
        }
    }
}
