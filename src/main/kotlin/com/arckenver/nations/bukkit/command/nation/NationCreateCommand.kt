package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.ConfirmationManager
import com.arckenver.nations.bukkit.manager.EconomyManager
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.SelectionManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.`object`.Nation
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.text.Text

object NationCreateCommand : Command("create") {
    private val argName = CommandArgument.string("name")

    init {
        withDescription(Text.t("nations.cmd_desc_nation_create"))
        withArgument(argName)
    }

    override fun execute(ctx: CommandContext) {
        val name = ctx.argument(argName)

        val player = ctx.senderPlayerNoNation()
        val selection = ctx.senderSelection()

        if (NationManager.getNation(name) != null) {
            throw CommandException.t("nations.nation_already_exists")
        }

        val estimatedCreationPrice = EconomyManager.nationCreationPrice
        val estimatedClaimPrice = EconomyManager.nationPricePerClaim * selection.value.area
        val estimatedTotalPrice = estimatedCreationPrice + estimatedClaimPrice

        if (EconomyManager.balance(player.uniqueId) < estimatedTotalPrice) {
            throw CommandException.t(
                "nations.player_insufficient_funds",
                estimatedTotalPrice
            )
        }

        ConfirmationManager.push(
            player,
            Text.build {
                +Text.t("nations.confirmation_operation_nation_create").yellow()

                +"\n"
                +Text.t(
                    "nations.confirmation_operation_nation_create_cost_creation",
                    estimatedCreationPrice,
                ).yellow()

                +"\n"
                +Text.t(
                    "nations.confirmation_operation_nation_claim_cost_area",
                    estimatedClaimPrice,
                    Text(selection.value.area)
                ).yellow()
            },
        ) {
            val creationPrice = EconomyManager.nationCreationPrice
            val claimPrice = EconomyManager.nationPricePerClaim * selection.value.area
            val totalPrice = creationPrice + claimPrice

            if (EconomyManager.balance(player.uniqueId) < totalPrice) {
                throw CommandException.t(
                    "nations.player_insufficient_funds",
                    totalPrice
                )
            }

            val nation = Nation(name, player.uniqueId)
            if (selection.value.area > nation.claimsLimit()) {
                throw CommandException.t("nations.nation_max_claims_reached")
            }
            if (!TerritoryManager.canClaimNation(selection, nation)) {
                throw CommandException.t("nations.cannot_claim_nation")
            }

            EconomyManager.withdraw(player.uniqueId, totalPrice)

            NationManager.saveNation(nation)
            TerritoryManager.addTerritory(selection, Territory(nation))

            SelectionManager.clearSelection(player.uniqueId)

            Nations.sendMessage(ctx.sender, Text.t("nations.nation_created", Text(name)).green())
        }
    }
}
