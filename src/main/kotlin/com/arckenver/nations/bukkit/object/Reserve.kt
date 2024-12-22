package com.arckenver.nations.bukkit.`object`

import com.arckenver.nations.bukkit.text.Text
import com.arckenver.nations.bukkit.text.Textable
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Reserve(
    val id: @Contextual UUID,
    var name: String,
) : Textable {
    constructor(name: String) : this(UUID.randomUUID(), name)

    override fun toText(clickable: Boolean) = Text(name).gold()
}
