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

object NationInviteCommand : Command("invite") {
    private val argPlayer = CommandArgument.onlinePlayer("player")

    init {
        withDescription(Text.t("nations.cmd_desc_nation_invite"))
        withArgument(argPlayer)
    }

    override fun execute(ctx: CommandContext) {
        val (player, nation) = ctx.senderPlayerNationStaff()

        val recipient = ctx.argument(argPlayer)
        NationManager.getPlayerNation(recipient.uniqueId)?.let {
            throw CommandException.t(
                "nations.player_already_belongs_to_nation",
                it.toText(clickable = true)
            )
        }

        when (InviteManager.send(Invite.Kind.INVITATION, nation.id, recipient.uniqueId)) {
            Invite.Result.ACCEPTED -> {
                val citizens = nation.citizens.toList()

                nation.citizens.add(recipient.uniqueId)
                NationManager.saveNation(nation)

                Nations.sendMessage(recipient, Text.t("nations.nation_joined", nation.toText(clickable = true)).green())

                for (playerId in citizens) {
                    Nations.sendMessageIfOnline(
                        playerId,
                        Text.t("nations.player_joined_nation", Text(recipient.name)).yellow()
                    )
                }
            }

            Invite.Result.SENT -> {
                Nations.sendMessage(recipient, Text.build {
                    +Text.t(
                        "nations.nation_invitation_received", nation.toText(clickable = true),
                        Text(player.name)
                    )
                        .gold()
                    +"\n\n    "
                    +Text.t("nations.accept_invitation")
                        .yellow()
                        .bold()
                        .runCommand("/nation join ${nation.name}")
                })

                for (playerId in nation.staff) {
                    Nations.sendMessageIfOnline(
                        playerId,
                        Text.t(
                            "nations.player_invited_to_join_nation",
                            Text(recipient.name),
                            Text(player.name)
                        )
                            .yellow()
                    )
                }
            }

            Invite.Result.ALREADY_SENT -> {
                throw CommandException.t("nations.player_already_invited")
            }
        }
    }
}
