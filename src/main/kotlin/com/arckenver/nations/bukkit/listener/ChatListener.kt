package com.arckenver.nations.bukkit.listener

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.manager.NationManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object ChatListener : Listener {
    @EventHandler
    fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {
        event.format = buildString {
            NationManager.getPlayerNation(event.player.uniqueId)?.let {
                append("§f[§3${it.name}§f] ")
            }
            append(permissionGroup(event.player))
            append("%1\$s§f: %2\$s")
        }
    }

    private fun permissionGroup(player: Player): String {
        val group = Nations.vaultPermission?.getPrimaryGroup(player) ?: return ""
        val prefix = groupManagerGroupPrefix(player)
        val suffix = groupManagerGroupSuffix(player)
        return "$prefix$group$suffix "
    }

    private fun groupManagerGroupPrefix(player: Player) =
        groupManagerUserGroup(player)?.variables?.getVarString("prefix")?.replace("&", "§") ?: ""

    private fun groupManagerGroupSuffix(player: Player) =
        groupManagerUserGroup(player)?.variables?.getVarString("suffix")?.replace("&", "§") ?: ""

    private fun groupManagerUserGroup(player: Player) =
        Nations.groupManager
            ?.worldsHolder
            ?.getWorldData(player)
            ?.getUser(player.uniqueId.toString())
            ?.group
}
