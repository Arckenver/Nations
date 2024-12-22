package com.arckenver.nations.bukkit.`object`

import com.arckenver.nations.bukkit.text.Text
import com.arckenver.nations.bukkit.text.Textable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Action : Textable {
    @SerialName("build")
    BUILD,

    @SerialName("interact")
    INTERACT;

    override fun toText(clickable: Boolean) = when (this) {
        BUILD -> Text.t("nations.build")
        INTERACT -> Text.t("nations.interact")
    }
}

@Serializable
enum class Actor : Textable {
    @SerialName("outsider")
    OUTSIDER,

    @SerialName("citizen")
    CITIZEN,

    @SerialName("coowner")
    COOWNER;

    override fun toText(clickable: Boolean) = when (this) {
        OUTSIDER -> Text.t("nations.outsiders")
        CITIZEN -> Text.t("nations.citizens")
        COOWNER -> Text.t("nations.coowners")
    }
}

val worldActions = listOf(
    Action.BUILD,
    Action.INTERACT
)

val defaultWorldPermissions = worldActions.associateWith { true }

val nationActions = listOf(
    Action.BUILD,
    Action.INTERACT
)

val nationActors = listOf(
    Actor.OUTSIDER,
    Actor.CITIZEN
)

val defaultNationPermissions = mapOf(
    Actor.OUTSIDER to nationActions.associateWith { false },
    Actor.CITIZEN to nationActions.associateWith { true }
)


val zoneActions = listOf(
    Action.BUILD,
    Action.INTERACT
)

val zoneActors = listOf(
    Actor.OUTSIDER,
    Actor.CITIZEN,
    Actor.COOWNER
)

val defaultZonePermissions = mapOf(
    Actor.OUTSIDER to zoneActions.associateWith { false },
    Actor.CITIZEN to zoneActions.associateWith { false },
    Actor.COOWNER to zoneActions.associateWith { true }
)

fun cleanActions(
    actions: MutableMap<Action, Boolean>,
    defaultActions: Map<Action, Boolean>
) {
    for ((action, value) in defaultActions) {
        actions.putIfAbsent(action, value)
    }
    for (action in actions.keys) {
        if (!defaultActions.containsKey(action)) {
            actions.remove(action)
        }
    }
}

fun cleanPermissions(
    permissions: MutableMap<Actor, MutableMap<Action, Boolean>>,
    defaultPermissions: Map<Actor, Map<Action, Boolean>>
) {
    for ((actor, defaultActions) in defaultPermissions) {
        val actions = permissions.getOrPut(actor) { mutableMapOf() }
        cleanActions(actions, defaultActions)
    }
    for (actor in permissions.keys) {
        if (!defaultPermissions.containsKey(actor)) {
            permissions.remove(actor)
        }
    }
}
