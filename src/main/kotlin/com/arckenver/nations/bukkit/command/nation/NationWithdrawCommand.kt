package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.EconomyManager
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.`object`.Balance
import com.arckenver.nations.bukkit.text.Text

object NationWithdrawCommand : Command("withdraw") {
    private val argAmount = CommandArgument.balance("amount")

    init {
        withDescription(Text.t("nations.cmd_desc_nation_withdraw"))
        withArgument(argAmount)
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationStaff()

        val amount = ctx.argument(argAmount)
        if (amount <= Balance.ZERO) {
            throw CommandException.t("nations.amount_must_be_positive")
        }
        if (nation.balance < amount) {
            throw CommandException.t("nations.nation_insufficient_funds", amount)
        }

        EconomyManager.deposit(player.uniqueId, amount)
        nation.balance -= amount
        NationManager.saveNation(nation)

        Nations.sendMessage(
            ctx.sender,
            Text.t("nations.nation_withdraw_successful", amount).green()
        )
    }
}
