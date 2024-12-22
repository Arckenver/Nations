package com.arckenver.nations.bukkit.listener

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.manager.SelectionManager
import com.arckenver.nations.bukkit.text.Text
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

object SelectionListener : Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.item?.type != SelectionManager.selectionItem) {
            return
        }

        val loc = event.clickedBlock?.location ?: return
        val point = Nations.locationPoint(loc)

        when (event.action) {
            Action.RIGHT_CLICK_BLOCK -> {
                SelectionManager.setRightClickSelection(event.player.uniqueId, point)
            }

            Action.LEFT_CLICK_BLOCK -> {
                SelectionManager.setLeftClickSelection(event.player.uniqueId, point)
            }

            else -> return
        }

        event.isCancelled = true

        Nations.sendMessage(
            event.player,
            Text.t(
                "nations.point_selected",
                Text(point.value.x),
                Text(point.value.z)
            ).yellow()
        )

        val selection = SelectionManager.selection(event.player.uniqueId)
        if (selection != null) {
            Nations.sendMessage(
                event.player,
                Text.t(
                    "nations.rect_selected",
                    Text(selection.value.area),
                    Text(selection.value.width),
                    Text(selection.value.height)
                ).yellow()
            )
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        SelectionManager.clearSelection(event.player.uniqueId)
    }
}
