package com.arckenver.nations.bukkit.command.nationadmin

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.`object`.World
import com.arckenver.nations.bukkit.text.Text

object NationadminWorldInfoCommand : Command("info") {
    private val argWorld = CommandArgument.world("world")

    init {
        withDescription(Text.t("nations.cmd_desc_nationadmin_world_info"))
        withArgument(argWorld)
    }

    override fun execute(ctx: CommandContext) {
        val world = ctx.argument(argWorld)

        Nations.sendMessage(ctx.sender, infoText(world))
    }

    internal fun infoText(world: World) = Text.build {
        +Text.header(world)

        +"\n"
        +Text.t("nations.permissions").gold()
        +Text(": ").gold()
        +Text.t("nations.display").gray()
            .bold()
            .runCommand("/nationadmin world perm ${world.name}")
        +Text(" <- ").darkGray().italic()
        +Text.t("nations.click_here").darkGray().italic()

        +"\n"
        +Text.t("nations.flags").gold()
        +Text(": ").gold()
        +Text.t("nations.display").gray()
            .bold()
            .runCommand("/nationadmin world flag ${world.name}")
        +Text(" <- ").darkGray().italic()
        +Text.t("nations.click_here").darkGray().italic()
    }
}
