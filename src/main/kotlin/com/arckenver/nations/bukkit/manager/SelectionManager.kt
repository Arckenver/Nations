package com.arckenver.nations.bukkit.manager

import com.arckenver.nations.bukkit.geometry.Rectangle
import com.arckenver.nations.bukkit.geometry.Vector
import com.arckenver.nations.bukkit.`object`.Worldly
import com.arckenver.nations.bukkit.text.Text
import java.util.UUID
import org.bukkit.Material

object SelectionManager {
    val selectionItem = Material.GOLDEN_AXE

    val selectionItemText
        get() = selectionItem.itemTranslationKey?.let { Text.t(it) } ?: Text(selectionItem.name)

    private val rightClickSelections = mutableMapOf<UUID, Worldly<Vector>>()
    private val leftClickSelections = mutableMapOf<UUID, Worldly<Vector>>()

    fun setRightClickSelection(playerId: UUID, point: Worldly<Vector>) {
        rightClickSelections[playerId] = point

        if (leftClickSelections[playerId]?.worldId != point.worldId) {
            leftClickSelections.remove(playerId)
        }
    }

    fun setLeftClickSelection(playerId: UUID, point: Worldly<Vector>) {
        leftClickSelections[playerId] = point

        if (rightClickSelections[playerId]?.worldId != point.worldId) {
            rightClickSelections.remove(playerId)
        }
    }

    fun clearSelection(playerId: UUID) {
        rightClickSelections.remove(playerId)
        leftClickSelections.remove(playerId)
    }

    fun selection(playerId: UUID): Worldly<Rectangle>? {
        val point1 = rightClickSelections[playerId] ?: return null
        val point2 = leftClickSelections[playerId] ?: return null
        if (point1.worldId != point2.worldId) {
            return null
        }
        return Worldly(point1.worldId, Rectangle(point1.value, point2.value))
    }
}