package com.arckenver.nations.bukkit.command.nationadmin

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.WorldManager
import com.arckenver.nations.bukkit.`object`.Action
import com.arckenver.nations.bukkit.`object`.World
import com.arckenver.nations.bukkit.`object`.worldActions
import com.arckenver.nations.bukkit.text.Text

object NationadminWorldPermCommand : Command("perm") {
    private val argWorld = CommandArgument.world("world")
    private val argAction = CommandArgument.enum<Action>(
        optional = true,
        entries = worldActions.toTypedArray(),
    )
    private val argValue = CommandArgument.boolean(optional = true)

    init {
        withDescription(Text.t("nations.cmd_desc_nationadmin_world_perm"))
        withArgument(argWorld)
        withArgument(argAction)
        withArgument(argValue)
    }

    override fun execute(ctx: CommandContext) {
        val world = ctx.argument(argWorld)

        val action = ctx.optionalArgument(argAction)
        if (action == null) {
            Nations.sendMessage(ctx.sender, permissionsText(world))
            return
        }

        val value = ctx.optionalArgument(argValue) ?: throw CommandException.t(
            "nations.missing_argument",
            Text(argValue.name)
        )

        world.permissions[action] = value
        WorldManager.saveWorld(world)

        Nations.sendMessage(ctx.sender, permissionsText(world))
    }

    private fun permissionsText(world: World) = Text.build {
        +Text.header(Text.build {
            +world
            +Text(" - ").gray()
            +Text.t("nations.permissions").yellow()
        })

        for ((action, value) in world.permissions) {
            +"\n"
            +action.toText().gold()
            +Text(": ").gold()
            +Text.yesNo(value)
                .bold()
                .runCommand("/nationadmin world perm ${world.name} ${action.name.lowercase()} ${!value}")
            +Text(" <- ").darkGray().italic()
            +Text.t("nations.click_here").darkGray().italic()
        }
    }
}
