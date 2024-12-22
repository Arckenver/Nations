package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.text.Text

object NationListCommand : Command("list") {
    init {
        withDescription(Text.t("nations.cmd_desc_nation_list"))
    }

    override fun execute(ctx: CommandContext) {
        val nations = NationManager.listNations()
            .sortedWith { n1, n2 -> n1.citizens.size.compareTo(n2.citizens.size) }
        if (nations.isEmpty()) {
            Nations.sendMessage(ctx.sender, Text.t("nations.no_nations").yellow())
            return
        }

        Nations.sendMessage(ctx.sender, Text.build {
            +Text.header(Text.t("nations.nations").yellow())
            +"\n"
            +Text.list(nations.map {
                it.toText(clickable = true) + Text("[${it.citizens.size}]").gold()
            })
        })
    }
}
