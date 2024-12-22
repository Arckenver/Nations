package com.arckenver.nations.bukkit.command.zone

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.manager.TerritoryManager
import com.arckenver.nations.bukkit.`object`.Territory
import com.arckenver.nations.bukkit.`object`.Zone
import com.arckenver.nations.bukkit.text.Text

object ZoneInfoCommand : Command("info") {
    private val argZone = CommandArgument.zone("zone", optional = true)

    init {
        withDescription(Text.t("nations.cmd_desc_zone_info"))
        withArgument(argZone)
    }

    override fun execute(ctx: CommandContext) {
        val zone = ctx.optionalArgument(argZone) ?: ctx.senderZoneAtPlayerLocation()

        Nations.sendMessage(ctx.sender, infoText(zone))
    }

    internal fun infoText(zone: Zone) = Text.build {
        +Text.header(Text.nationZone(NationManager.getNation(zone.nationId)!!, zone))

        +"\n"
        +Text.t("nations.area").gold()
        +Text(": ").gold()
        +Text(TerritoryManager.areaOf(Territory(zone))).yellow()

        +"\n"
        +Text.t("nations.owner").gold()
        +Text(": ").gold()
        val ownerId = zone.ownerId
        if (ownerId == null) {
            +Text.t("nations.none").gray()
        } else {
            +Nations.playerName(ownerId).yellow()
        }

        +"\n"
        +Text.t("nations.coowners").gold()
        +Text(": ").gold()
        if (zone.coowners.isEmpty()) {
            +Text.t("nations.none").gray()
        } else {
            +Text.list(zone.coowners.map { Nations.playerName(it).yellow() })
        }

        +"\n"
        +Text.t("nations.price").gold()
        +Text(": ").gold()
        val price = zone.price
        if (price != null) {
            +price.toText().yellow()
        } else {
            +Text.t("nations.not_for_sale").gray()
        }

        +"\n"
        +Text.t("nations.permissions").gold()
        +Text(": ").gold()
        +Text.t("nations.display").gray()
            .bold()
            .runCommand("/zone perm")
        +Text(" <- ").darkGray().italic()
        +Text.t("nations.click_here").darkGray().italic()

        +"\n"
        +Text.t("nations.flags").gold()
        +Text(": ").gold()
        +Text.t("nations.display").gray()
            .bold()
            .runCommand("/zone flag")
        +Text(" <- ").darkGray().italic()
        +Text.t("nations.click_here").darkGray().italic()
    }
}
