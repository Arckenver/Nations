package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.ConfigManager
import com.arckenver.nations.bukkit.manager.EconomyManager
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.`object`.Nation
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.text.Text
import org.bukkit.entity.Player

object NationInfoCommand : Command("info") {
    private val argNation = CommandArgument.nation("nation", optional = true)

    init {
        withDescription(Text.t("nations.cmd_desc_nation_info"))
        withArgument(argNation)
    }

    override fun execute(ctx: CommandContext) {
        val nation = ctx.optionalArgument(argNation)
            ?: (ctx.sender as? Player)?.let { NationManager.getPlayerNation(it.uniqueId) }
            ?: throw CommandException.t("nations.missing_argument", Text(argNation.name))

        Nations.sendMessage(ctx.sender, infoText(nation))
    }

    internal fun infoText(nation: Nation) = Text.build {
        +Text.header(nation)

        +"\n"
        +Text.build {
            +Text.t("nations.area").gold()
            +Text(": ").gold()
            +Text(TerritoryManager.areaOf(Territory(nation))).yellow()
            +Text(" / ").yellow()
            +Text(nation.claimsLimit()).yellow()
        }.hover(Text.build {
            +Text.t("nations.free_claims_per_citizen").gold()
            +Text(": ").gold()
            +Text(ConfigManager.nationFreeClaimsPerCitizen.get()).yellow()
            +Text(" * ").yellow()
            +Text(nation.citizens.size).yellow()
            +Text(" = ").yellow()
            val freeClaims = nation.citizens.size * ConfigManager.nationFreeClaimsPerCitizen.get()
            +Text(freeClaims).yellow()

            if (nation.paidClaims > 0) {
                +"\n"
                +Text.t("nations.paid_extra_claims").gold()
                +Text(": ").gold()
                +Text(nation.paidClaims).yellow()
            }
        })

        +"\n"
        +Text.t("nations.balance").gold()
        +Text(": ").gold()
        +nation.balance.toText().yellow()

        +"\n"
        val upkeep = EconomyManager.nationUpkeep(nation)
        +Text.build {
            +Text.t("nations.upkeep").gold()
            +Text(": ").gold()
            +upkeep.total.toText().yellow()
        }.hover(Text.build {
            +Text.t("nations.upkeep_per_citizen").gold()
            +Text(": ").gold()
            +EconomyManager.taxNationUpkeepPerCitizen.toText().yellow()
            +Text(" * ").yellow()
            +Text(nation.citizens.size).yellow()
            +Text(" = ").yellow()
            +upkeep.citizenUpkeep.toText().yellow()

            +"\n"
            +Text.t("nations.upkeep_per_claim").gold()
            +Text(": ").gold()
            +EconomyManager.taxNationUpkeepPerClaim.toText().yellow()
            +Text(" * ").yellow()
            +Text(TerritoryManager.areaOf(Territory(nation))).yellow()
            +Text(" = ").yellow()
            +upkeep.claimUpkeep.toText().yellow()
        })

        +"\n"
        +Text.t("nations.president").gold()
        +Text(": ").gold()
        +Nations.playerName(nation.presidentId).yellow()

        +"\n"
        +Text.t("nations.ministers").gold()
        +Text(": ").gold()
        if (nation.ministers.isEmpty()) {
            +Text.t("nations.none").gray()
        } else {
            +Text.list(nation.ministers.map { Nations.playerName(it).yellow() })
        }

        +"\n"
        +Text.t("nations.citizens").gold()
        +Text(": ").gold()
        if (nation.citizens.isEmpty()) {
            +Text.t("nations.none").gray()
        } else {
            +Text.list(nation.citizens.map { Nations.playerName(it).yellow() })
        }

        +"\n"
        +Text.t("nations.permissions").gold()
        +Text(": ").gold()
        +Text.t("nations.display").gray()
            .bold()
            .runCommand("/nation perm")
        +Text(" <- ").darkGray().italic()
        +Text.t("nations.click_here").darkGray().italic()

        +"\n"
        +Text.t("nations.flags").gold()
        +Text(": ").gold()
        +Text.t("nations.display").gray()
            .bold()
            .runCommand("/nation flag")
        +Text(" <- ").darkGray().italic()
        +Text.t("nations.click_here").darkGray().italic()
    }
}
