package com.arckenver.nations.bukkit.manager

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.`object`.Balance
import com.arckenver.nations.bukkit.`object`.Nation
import com.arckenver.nations.bukkit.`object`.Territory
import java.util.UUID
import net.milkbowl.vault.economy.EconomyResponse

private const val fallbackFractionalDigits = 2

class EconomyFailedTransactionException(resp: EconomyResponse) :
    Exception("Economy transaction failed: ${resp.errorMessage}")

object EconomyManager {
    private val eco get() = Nations.vaultEconomy

    val nationCreationPrice get() = Balance.fromDouble(ConfigManager.nationCreationPrice.get())
    val nationPricePerClaim get() = Balance.fromDouble(ConfigManager.nationPricePerClaim.get())
    val nationOutpostPrice get() = Balance.fromDouble(ConfigManager.nationOutpostPrice.get())
    val nationExtraClaimsPrice get() = Balance.fromDouble(ConfigManager.nationExtraClaimsPrice.get())
    val taxNationUpkeepPerCitizen get() = Balance.fromDouble(ConfigManager.taxNationUpkeepPerCitizen.get())
    val taxNationUpkeepPerClaim get() = Balance.fromDouble(ConfigManager.taxNationUpkeepPerClaim.get())

    fun balance(playerId: UUID) = Balance.fromDouble(eco.getBalance(Nations.plugin.server.getOfflinePlayer(playerId)))

    fun withdraw(playerId: UUID, amount: Balance) =
        assertOk(eco.withdrawPlayer(Nations.plugin.server.getOfflinePlayer(playerId), amount.toDouble()))

    fun deposit(playerId: UUID, amount: Balance) =
        assertOk(eco.depositPlayer(Nations.plugin.server.getOfflinePlayer(playerId), amount.toDouble()))

    @Throws(EconomyFailedTransactionException::class)
    private fun assertOk(resp: EconomyResponse) {
        if (!resp.transactionSuccess()) {
            throw EconomyFailedTransactionException(resp)
        }
    }

    fun format(amount: Double): String = eco.format(amount)

    data class NationUpkeep(
        val citizenUpkeep: Balance,
        val claimUpkeep: Balance,
    ) {
        val total = citizenUpkeep + claimUpkeep
    }

    fun nationUpkeep(nation: Nation) = NationUpkeep(
        citizenUpkeep = taxNationUpkeepPerCitizen * nation.citizens.size,
        claimUpkeep = taxNationUpkeepPerClaim * TerritoryManager.areaOf(Territory(nation)),
    )

    fun fractionalDigits(): Int {
        val digits = eco.fractionalDigits()
        return if (digits >= 0) digits else fallbackFractionalDigits
    }
}
