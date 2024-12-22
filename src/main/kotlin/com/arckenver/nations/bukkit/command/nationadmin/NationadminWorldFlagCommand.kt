package com.arckenver.nations.bukkit.command.nationadmin

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.WorldManager
import com.arckenver.nations.bukkit.`object`.Flag
import com.arckenver.nations.bukkit.`object`.World
import com.arckenver.nations.bukkit.text.Text

object NationadminWorldFlagCommand : Command("flag") {
    private val argWorld = CommandArgument.world("world")
    private val argFlag = CommandArgument.enum<Flag>(optional = true)
    private val argValue = CommandArgument.boolean(optional = true)

    init {
        withDescription(Text.t("nations.cmd_desc_nationadmin_world_flag"))
        withArgument(argWorld)
        withArgument(argFlag)
        withArgument(argValue)
    }

    override fun execute(ctx: CommandContext) {
        val world = ctx.argument(argWorld)

        val flag = ctx.optionalArgument(argFlag)
        if (flag == null) {
            Nations.sendMessage(ctx.sender, flagsText(world))
            return
        }

        val value = ctx.optionalArgument(argValue) ?: throw CommandException.t(
            "nations.missing_argument",
            Text(argValue.name)
        )

        world.flags[flag] = value
        WorldManager.saveWorld(world)

        Nations.sendMessage(ctx.sender, flagsText(world))
    }

    private fun flagsText(world: World) = Text.build {
        +Text.header(Text.build {
            +world
            +Text(" - ").gray()
            +Text.t("nations.flags").yellow()
        })

        for ((flag, value) in world.flags) {
            +"\n"
            +flag.toText().gold()
            +Text(": ").gold()
            +Text.yesNo(value)
                .bold()
                .runCommand("/nationadmin world flag ${world.name} ${flag.name.lowercase()} ${!value}")
            +Text(" <- ").darkGray().italic()
            +Text.t("nations.click_here").darkGray().italic()
        }
    }
}
