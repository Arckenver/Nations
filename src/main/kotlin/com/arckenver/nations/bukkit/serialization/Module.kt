package com.arckenver.nations.bukkit.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

private val module = SerializersModule {
    contextual(UUIDSerializer)
}

@OptIn(ExperimentalSerializationApi::class)
val Json = Json {
    serializersModule = module
    prettyPrint = true
    prettyPrintIndent = "  "
}
