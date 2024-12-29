package com.arckenver.nations.bukkit.command.nationadmin

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.ClaimCheck
import com.arckenver.nations.bukkit.manager.ReserveManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.text.Text

object NationadminReserveClaimCommand : Command("claim") {
    private val argReserveName = CommandArgument.string(
        "reserve name",
        optional = false,
        tabCompleter = { _, arg ->
            ReserveManager.listReserves()
                .map { it.name }
                .filter { it.startsWith(arg, ignoreCase = true) }
        },
    )

    init {
        withDescription(Text.t("nations.cmd_desc_nationadmin_reserve_claim"))
        withArgument(argReserveName)
    }

    override fun execute(ctx: CommandContext) {
        val reserveName = ctx.argument(argReserveName)

        val selection = ctx.senderSelection()

        val reserve = ReserveManager.getOrCreateReserve(reserveName)

        val claimCheck = TerritoryManager.canClaimReserve(selection, reserve)
        if (claimCheck is ClaimCheck.CannotClaim) when (claimCheck.reason) {
            is ClaimCheck.Reason.TooCloseToTerritory -> {
                throw CommandException.t("nations.cannot_claim_too_close", claimCheck.reason.territory)
            }

            is ClaimCheck.Reason.NotWithinTerritory -> {
                throw CommandException.t("nations.cannot_claim_not_within", claimCheck.reason.territory)
            }
        }

        TerritoryManager.addTerritory(selection, Territory(reserve))

        Nations.sendMessage(ctx.sender, Text.t("nations.claimed_area").green())
    }
}
