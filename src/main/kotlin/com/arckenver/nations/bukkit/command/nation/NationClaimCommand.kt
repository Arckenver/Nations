package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.geometry.Rectangle
import com.arckenver.nations.bukkit.manager.ConfirmationManager
import com.arckenver.nations.bukkit.manager.EconomyManager
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.SelectionManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.`object`.Balance
import com.arckenver.nations.bukkit.`object`.Nation
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.`object`.Worldly
import com.arckenver.nations.bukkit.text.Text

object NationClaimCommand : Command("claim") {
    init {
        withDescription(Text.t("nations.cmd_desc_nation_claim"))
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationStaff()
        val selection = ctx.senderSelection()

        val estimated = quote(selection, nation)

        ConfirmationManager.push(
            player,
            Text.build {
                +Text.t("nations.confirmation_operation_nation_claim", nation).yellow()

                +"\n"
                +Text.t(
                    "nations.confirmation_operation_nation_claim_cost_area",
                    estimated.claimsPrice,
                    Text(estimated.newClaims)
                ).yellow()
                
                if (estimated.extraClaimsPrice > Balance.ZERO) {
                    +"\n"
                    +Text.t(
                        "nations.confirmation_operation_nation_claim_cost_extra_claims",
                        estimated.extraClaimsPrice,
                        Text(estimated.newExtraClaims)
                    ).yellow()
                }

                if (estimated.outpostPrice > Balance.ZERO) {
                    +"\n"
                    +Text.t(
                        "nations.confirmation_operation_nation_claim_cost_outpost",
                        estimated.outpostPrice
                    ).yellow()
                }
            },
        ) {
            val actual = quote(selection, nation)

            if (nation.balance < actual.totalPrice) {
                throw CommandException.t(
                    "nations.nation_insufficient_funds",
                    actual.totalPrice
                )
            }
            nation.balance -= actual.totalPrice
            nation.paidClaims += actual.newExtraClaims

            NationManager.saveNation(nation)
            TerritoryManager.addTerritory(selection, Territory(nation))

            SelectionManager.clearSelection(player.uniqueId)

            Nations.sendMessage(ctx.sender, Text.t("nations.claimed_area").green())
        }
    }

    private data class Quote(
        val outpostPrice: Balance,
        val newClaims: Int,
        val newExtraClaims: Int,
        val claimsPrice: Balance,
        val extraClaimsPrice: Balance,
    ) {
        val totalPrice get() = outpostPrice + claimsPrice + extraClaimsPrice
    }

    @Throws(CommandException::class)
    private fun quote(selection: Worldly<Rectangle>, nation: Nation): Quote {
        val outpostPrice = if (!TerritoryManager.isAdjacent(selection, Territory(nation))) {
            EconomyManager.nationOutpostPrice
        } else {
            Balance.ZERO
        }

        var newClaims = selection.value.area - TerritoryManager.overlapArea(selection, Territory(nation))
        if (newClaims <= 0) {
            throw CommandException.t("nations.selected_area_already_claimed_nation")
        }
        val claimPrice = EconomyManager.nationPricePerClaim * newClaims

        if (!TerritoryManager.canClaimNation(selection, nation)) {
            throw CommandException.t("nations.cannot_claim_nation")
        }

        val totalNewClaims = TerritoryManager.areaOf(Territory(nation)) + newClaims
        if (totalNewClaims > nation.maxClaimsLimit()) {
            throw CommandException.t("nations.nation_max_claims_reached")
        }

        val newExtraClaims = (totalNewClaims - nation.claimsLimit()).let { if (it > 0) it else 0 }
        val estimatedExtraClaimsPrice = if (newExtraClaims > 0) {
            EconomyManager.nationExtraClaimsPrice * newExtraClaims
        } else {
            Balance.ZERO
        }

        return Quote(
            outpostPrice,
            newClaims,
            newExtraClaims,
            claimPrice,
            estimatedExtraClaimsPrice,
        )
    }
}
