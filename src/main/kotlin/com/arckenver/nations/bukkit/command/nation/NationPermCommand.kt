package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.`object`.Action
import com.arckenver.nations.bukkit.`object`.Actor
import com.arckenver.nations.bukkit.`object`.Nation
import com.arckenver.nations.bukkit.`object`.nationActions
import com.arckenver.nations.bukkit.`object`.nationActors
import com.arckenver.nations.bukkit.text.Text

object NationPermCommand : Command("perm") {
    private val argActor = CommandArgument.enum<Actor>(
        optional = true,
        entries = nationActors.toTypedArray(),
    )
    private val argAction = CommandArgument.enum<Action>(
        optional = true,
        entries = nationActions.toTypedArray(),
    )
    private val argValue = CommandArgument.boolean(optional = true)

    init {
        withDescription(Text.t("nations.cmd_desc_nation_perm"))
        withArgument(argActor)
        withArgument(argAction)
        withArgument(argValue)
    }

    override fun execute(ctx: CommandContext) {
        val (_, nation) = ctx.senderPlayerNationStaff()

        val actor = ctx.optionalArgument(argActor)
        if (actor == null) {
            Nations.sendMessage(ctx.sender, permissionsText(nation))
            return
        }

        val action = ctx.optionalArgument(argAction) ?: throw CommandException.t(
            "nations.missing_argument",
            Text(argAction.name)
        )

        val value = ctx.optionalArgument(argValue) ?: throw CommandException.t(
            "nations.missing_argument",
            Text(argValue.name)
        )

        nation.permissions[actor]!![action] = value
        NationManager.saveNation(nation)

        Nations.sendMessage(ctx.sender, permissionsText(nation))
    }

    private fun permissionsText(nation: Nation) = Text.build {
        +Text.header(Text.build {
            +nation
            +Text(" - ").gray()
            +Text.t("nations.permissions").yellow()
        })

        for ((actor, actions) in nation.permissions) {
            +"\n"
            +actor.toText().gold()
            +Text(":").gold()

            for ((action, value) in actions) {
                +"\n    "
                +action.toText().gold()
                +Text(": ").gold()
                +Text.yesNo(value)
                    .bold()
                    .runCommand("/nation perm ${actor.name.lowercase()} ${action.name.lowercase()} ${!value}")
            }
        }
    }
}
