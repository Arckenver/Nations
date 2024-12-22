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

object NationDepositCommand : Command("deposit") {
    private val argAmount = CommandArgument.balance("amount")

    init {
        withDescription(Text.t("nations.cmd_desc_nation_deposit"))
        withArgument(argAmount)
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationMember()

        val amount = ctx.argument(argAmount)
        if (amount <= Balance.ZERO) {
            throw CommandException.t("nations.amount_must_be_positive")
        }
        if (EconomyManager.balance(player.uniqueId) < amount) {
            throw CommandException.t("nations.player_insufficient_funds", amount)
        }

        EconomyManager.withdraw(player.uniqueId, amount)
        nation.balance += amount
        NationManager.saveNation(nation)

        Nations.sendMessage(
            ctx.sender,
            Text.t("nations.nation_deposit_successful", amount).green()
        )
    }
}
