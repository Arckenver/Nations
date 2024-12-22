package com.arckenver.nations.bukkit.command.nationadmin

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.manager.WorldManager
import com.arckenver.nations.bukkit.text.Text

object NationadminWorldListCommand : Command("list") {
    init {
        withDescription(Text.t("nations.cmd_desc_nationadmin_world_list"))
    }

    override fun execute(ctx: CommandContext) {
        val worlds = WorldManager.listWorlds()
            .sortedWith { w1, w2 -> w1.name.compareTo(w2.name) }
        if (worlds.isEmpty()) {
            Nations.sendMessage(ctx.sender, Text.t("nations.no_worlds").yellow())
            return
        }

        Nations.sendMessage(ctx.sender, Text.build {
            +Text.header(Text.t("nations.worlds").yellow())
            +"\n"
            +Text.list(worlds.map {
                it.toText(clickable = true)
            })
        })
    }
}
