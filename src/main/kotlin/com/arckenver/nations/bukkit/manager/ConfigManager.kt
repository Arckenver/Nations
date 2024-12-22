package com.arckenver.nations.bukkit.manager

import com.arckenver.nations.bukkit.Nations

object ConfigManager {
    val nationCreationPrice = Value<Double>(
        "nation_creation_price",
        1_000.0,
        comments = listOf("Price to create a nation"),
    )

    val nationPricePerClaim = Value<Double>(
        "nation_price_per_claim",
        1.0,
        comments = listOf("Price for claiming one block"),
    )

    val nationOutpostPrice = Value<Double>(
        "nation_outpost_price",
        800.0,
        comments = listOf("Price for claiming blocks that are not adjacent to the nation")
    )

    val nationExtraClaimsPrice = Value<Double>(
        "nation_extra_claims_price_per_claim",
        5.0,
        comments = listOf("Price for buying extra claims, does not include the price for claiming them")
    )

    val nationMaxExtraClaims = Value<Int>(
        "nation_max_extra_claims",
        100_000,
        comments = listOf("Maximum number of extra claims a nation can buy")
    )

    val nationClaimMinDistance = Value<Int>(
        "nation_claim_min_distance",
        100,
        comments = listOf("Minimum distance between two nations"),
    )

    val nationFreeClaimsPerCitizen = Value<Int>(
        "nation_free_claims_per_citizen",
        2_000,
        comments = listOf("Free claims per citizen"),
    )

    val taxCollectionCron = Value<String>(
        "tax_collection_cron",
        "0 0 12 * * ?",
        comments = listOf("Cron expression for tax collection, in quartz format, see https://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/crontrigger.html"),
    )

    val taxNationUpkeepPerCitizen = Value<Double>(
        "tax_nation_upkeep_per_citizen",
        1.0,
        comments = listOf("Tax per citizen for nation upkeep"),
    )

    val taxNationUpkeepPerClaim = Value<Double>(
        "tax_nation_upkeep_per_claim",
        0.01,
        comments = listOf("Tax per claim for nation upkeep"),
    )

    fun load() {
        this::class.java.declaredFields
            .filter { Value::class.java.isAssignableFrom(it.type) }
            .forEach {
                val value = it.get(this) as Value<*>
                if (!value.exists()) {
                    value.setDefault()
                }
            }
        Nations.plugin.saveConfig()
    }

    fun save() {
        Nations.plugin.saveConfig()
    }

    class Value<T : Any>(
        val path: String,
        val defaultValue: T,
        val comments: List<String> = emptyList(),
    ) {
        fun exists(): Boolean {
            @Suppress("UNCHECKED_CAST")
            return (Nations.plugin.config.get(path, null) as? T) != null
        }

        fun get(default: T? = null): T {
            @Suppress("UNCHECKED_CAST")
            return Nations.plugin.config.get(path, default ?: defaultValue) as T
        }

        fun set(value: T) {
            Nations.plugin.config.set(path, value)
            save()
        }

        fun setDefault() {
            set(defaultValue)
            if (comments.isNotEmpty()) {
                Nations.plugin.config.setComments(path, comments)
            }
        }
    }
}
