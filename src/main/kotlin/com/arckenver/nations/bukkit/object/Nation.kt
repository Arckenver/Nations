package com.arckenver.nations.bukkit.`object`

import com.arckenver.nations.bukkit.manager.ConfigManager
import com.arckenver.nations.bukkit.text.Text
import com.arckenver.nations.bukkit.text.Textable
import java.util.UUID
import kotlinx.serialization.Contextual
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Nation(
    val id: @Contextual UUID,
    var name: String,
    var presidentId: @Contextual UUID,
    @EncodeDefault
    val ministers: MutableList<@Contextual UUID> = mutableListOf(),
    @EncodeDefault
    val citizens: MutableList<@Contextual UUID> = mutableListOf(presidentId),
    @EncodeDefault
    var balance: Balance = Balance.ZERO,
    @EncodeDefault
    var paidClaims: Int = 0,
    @EncodeDefault
    val permissions: MutableMap<Actor, MutableMap<Action, Boolean>> =
        defaultNationPermissions
            .mapValues { it.value.toMutableMap() }
            .toMutableMap(),
    @EncodeDefault
    val flags: MutableMap<Flag, Boolean> = defaultNationFlags.toMutableMap(),
) : Textable {
    constructor(name: String, presidentId: UUID) : this(UUID.randomUUID(), name, presidentId)

    init {
        cleanFlags(flags, defaultNationFlags)
        cleanPermissions(permissions, defaultNationPermissions)
    }

    val staff: Sequence<UUID> get() = sequence { yield(presidentId); yieldAll(ministers) }

    fun isPresident(playerId: UUID) = presidentId == playerId

    fun isMinister(playerId: UUID) = ministers.contains(playerId)

    fun isStaff(playerId: UUID) = isPresident(playerId) || isMinister(playerId)

    fun isCitizen(playerId: UUID) = citizens.contains(playerId)

    fun claimsLimit() = ConfigManager.nationFreeClaimsPerCitizen.get() * citizens.size + paidClaims

    fun maxClaimsLimit() =
        ConfigManager.nationFreeClaimsPerCitizen.get() * citizens.size + ConfigManager.nationMaxExtraClaims.get()

    fun setPresident(playerId: UUID) {
        if (!citizens.contains(playerId)) {
            throw IllegalArgumentException("Cannot set non-citizen as president")
        }
        ministers.remove(playerId)
        presidentId = playerId
    }

    fun addMinister(playerId: UUID) {
        if (!citizens.contains(playerId)) {
            throw IllegalArgumentException("Cannot add non-citizen as minister")
        }
        if (playerId == presidentId) {
            throw IllegalArgumentException("Cannot add president as minister")
        }
        if (!ministers.contains(playerId)) {
            ministers.add(playerId)
        }
    }

    fun removeMinister(playerId: UUID) {
        ministers.remove(playerId)
    }

    fun removeCitizen(playerId: UUID) {
        if (playerId == presidentId) {
            throw IllegalArgumentException("Cannot remove president from nation")
        }
        ministers.remove(playerId)
        citizens.remove(playerId)
    }

    fun getPermission(actor: Actor, action: Action) = permissions[actor]?.get(action) == true

    fun getFlag(flag: Flag) = flags[flag] == true

    override fun toText(clickable: Boolean): Text {
        var t = Text(name).darkAqua()
        if (clickable) {
            t = t.runCommand("/nation info $name")
        }
        return t
    }
}
