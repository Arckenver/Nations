package com.arckenver.nations.bukkit.command.nation

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.command.Command
import com.arckenver.nations.bukkit.command.CommandArgument
import com.arckenver.nations.bukkit.command.CommandContext
import com.arckenver.nations.bukkit.command.CommandException
import com.arckenver.nations.bukkit.manager.InviteManager
import com.arckenver.nations.bukkit.manager.NationManager
import com.arckenver.nations.bukkit.`object`.Invite
import com.arckenver.nations.bukkit.text.Text

object NationJoinCommand : Command("join") {
    private val argNation = CommandArgument.nation("nation")

    init {
        withDescription(Text.t("nations.cmd_desc_nation_join"))
        withArgument(argNation)
    }

    override fun execute(ctx: CommandContext) {
        val player = ctx.senderPlayerNoNation()

        val nation = ctx.argument(argNation)

        when (InviteManager.send(Invite.Kind.REQUEST, nation.id, player.uniqueId)) {
            Invite.Result.ACCEPTED -> {
                val citizens = nation.citizens.toList()

                nation.citizens.add(player.uniqueId)
                NationManager.saveNation(nation)

                Nations.sendMessage(
                    player,
                    Text.t("nations.nation_joined", nation.toText(clickable = true)).green()
                )

                for (playerId in citizens) {
                    Nations.sendMessageIfOnline(
                        playerId,
                        Text.t("nations.player_joined_nation", Text(player.name)).yellow()
                    )
                }
            }

            Invite.Result.SENT -> {
                var requestSent = false
                for (playerId in nation.staff) {
                    Nations.plugin.server.getPlayer(playerId)?.let {
                        Nations.sendMessage(it, Text.build {
                            +Text.t("nations.player_request_received", Nations.playerName(player.uniqueId))
                                .gold()
                            +"\n\n    "
                            +Text.t("nations.accept_invitation")
                                .yellow()
                                .bold()
                                .runCommand("/nation invite ${player.name}")
                        })
                        requestSent = true
                    }
                }

                if (!requestSent) {
                    InviteManager.delete(nation.id, player.uniqueId)
                    throw CommandException.t("nations.nation_staff_not_connected")
                }

                Nations.sendMessage(ctx.sender, Text.t("nations.player_request_sent").green())
            }

            Invite.Result.ALREADY_SENT -> {
                throw CommandException.t("nations.nation_already_requested")
            }
        }
    }
}
