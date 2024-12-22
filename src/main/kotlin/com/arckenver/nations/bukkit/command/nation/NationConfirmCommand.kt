package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.manager.ConfirmationManager
import com.arckenver.nations.bukkit.`object`.Confirmation
import com.arckenver.nations.bukkit.text.Text


object NationConfirmCommand : Command("confirm") {
    private val argDecision = CommandArgument.enum<Confirmation.Decision>()
    private val argConfirmationId = CommandArgument.uuid("confirmation id")

    init {
        withDescription(Text.t("nations.cmd_desc_nation_confirm"))
        withArgument(argDecision)
        withArgument(argConfirmationId)
    }

    override fun execute(ctx: CommandContext) {
        val player = ctx.senderPlayer()

        val decision = ctx.argument(argDecision)
        val confirmationId = ctx.argument(argConfirmationId)

        when (decision) {
            Confirmation.Decision.APPROVE -> ConfirmationManager.approve(confirmationId, player.uniqueId)
            Confirmation.Decision.CANCEL -> ConfirmationManager.cancel(confirmationId, player.uniqueId)
        }
    }
}
