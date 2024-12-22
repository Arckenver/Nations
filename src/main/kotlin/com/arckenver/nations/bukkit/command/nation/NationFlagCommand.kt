package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.`object`.Flag
import com.arckenver.nations.bukkit.`object`.Nation
import com.arckenver.nations.bukkit.text.Text

object NationFlagCommand : Command("flag") {
    private val argFlag = CommandArgument.enum<Flag>(optional = true)
    private val argValue = CommandArgument.boolean(optional = true)

    init {
        withDescription(Text.t("nations.cmd_desc_nation_flag"))
        withArgument(argFlag)
        withArgument(argValue)
    }

    override fun execute(ctx: CommandContext) {
        val (_, nation) = ctx.senderPlayerNationStaff()

        val flag = ctx.optionalArgument(argFlag)
        if (flag == null) {
            Nations.sendMessage(ctx.sender, flagsText(nation))
            return
        }

        val value = ctx.optionalArgument(argValue) ?: throw CommandException.t(
            "nations.missing_argument",
            Text(argValue.name)
        )

        nation.flags[flag] = value
        NationManager.saveNation(nation)

        Nations.sendMessage(ctx.sender, flagsText(nation))
    }

    private fun flagsText(nation: Nation) = Text.build {
        +Text.header(Text.build {
            +nation
            +Text(" - ").gray()
            +Text.t("nations.flags").yellow()
        })

        for ((flag, value) in nation.flags) {
            +"\n"
            +flag.toText().gold()
            +Text(": ").gold()
            +Text.yesNo(value)
                .bold()
                .runCommand("/nation flag ${flag.name.lowercase()} ${!value}")
            +Text(" <- ").darkGray().italic()
            +Text.t("nations.click_here").darkGray().italic()
        }
    }
}
