package com.arckenver.nations.bukkit.`object`

import com.arckenver.nations.bukkit.manager.EconomyManager
import com.arckenver.nations.bukkit.text.Text
import com.arckenver.nations.bukkit.text.Textable
import kotlin.math.pow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Balance.Serializer::class)
class Balance private constructor(
    val amount: Int,
) : Textable {
    companion object {
        private val frac
            get() = EconomyManager.fractionalDigits()

        val ZERO = Balance(0)

        fun fromDouble(amount: Double) = Balance((amount * 10.0.pow(frac)).toInt())
    }

    operator fun plus(other: Balance) = Balance(amount + other.amount)
    operator fun minus(other: Balance) = Balance(amount - other.amount)
    operator fun times(other: Balance) = Balance(amount * other.amount)
    operator fun div(other: Balance) = Balance(amount / other.amount)

    operator fun times(other: Int) = Balance(amount * other)
    operator fun div(other: Int) = Balance(amount / other)

    operator fun compareTo(other: Balance) = amount.compareTo(other.amount)

    fun toDouble() = amount.toDouble() / 10.0.pow(frac)

    override fun toString(): String = EconomyManager.format(toDouble())

    override fun toText(clickable: Boolean) = Text(toString())

    private class Serializer : KSerializer<Balance> {
        override val descriptor = PrimitiveSerialDescriptor(Balance::class.qualifiedName!!, PrimitiveKind.DOUBLE)

        override fun deserialize(decoder: Decoder): Balance {
            return fromDouble(decoder.decodeDouble())
        }

        override fun serialize(encoder: Encoder, value: Balance) {
            encoder.encodeDouble(value.toDouble())
        }
    }
}