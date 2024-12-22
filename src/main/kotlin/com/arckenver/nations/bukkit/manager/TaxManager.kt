package com.arckenver.nations.bukkit.manager

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.text.Text
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import java.time.ZonedDateTime

object TaxManager : TaskTimerAsyncManager(100, 100) {
    private var nextExecution: ZonedDateTime? = null

    override fun runTask() {
        if (nextExecution == null) {
            nextExecution = evaluateNextExecution()
        }

        nextExecution?.let {
            if (it.isBefore(ZonedDateTime.now())) {
                collectTaxes()
                nextExecution = null
            }
        }
    }

    fun collectTaxes() {
        Nations.plugin.logger.info("Starting tax collection")

        for (nation in NationManager.listNations().toList()) {
            val upkeep = EconomyManager.nationUpkeep(nation)
            Nations.plugin.logger.info(
                "Nation ${nation.name} with balance ${nation.balance} owes upkeep $upkeep"
            )

            if (nation.balance >= upkeep.total) {
                nation.balance -= upkeep.total
                NationManager.saveNation(nation)

                Nations.broadcastMessageNation(
                    nation,
                    Text.t("nations.upkeep_collected", upkeep.total).yellow()
                )

            } else {
                for (zone in ZoneManager.listZones(nation.id)) {
                    TerritoryManager.deleteTerritory(Territory(zone))
                }
                TerritoryManager.deleteTerritory(Territory(nation))

                ZoneManager.deleteZones(nation.id)
                NationManager.deleteNation(nation.id)

                Nations.broadcastMessage(
                    Text.t("nations.nation_deleted_because_no_upkeep", nation).red()
                )
            }
        }
    }

    fun evaluateNextExecution(): ZonedDateTime? {
        val cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)
        val parser = CronParser(cronDefinition)
        val cron = parser.parse(ConfigManager.taxCollectionCron.get())
        cron.validate()
        return ExecutionTime.forCron(cron).nextExecution(ZonedDateTime.now()).orElse(null)
    }
}
