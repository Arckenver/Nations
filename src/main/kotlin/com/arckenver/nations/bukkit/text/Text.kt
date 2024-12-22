package com.arckenver.nations.bukkit.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

val UnknownText = Text("???").gray()

interface Textable : ComponentLike {
    fun toText(clickable: Boolean = false): Text

    override fun asComponent() = toText().component
}

class Text private constructor(
    val component: Component
) : Textable {
    constructor() : this(Component.empty())

    constructor(value: String) : this(Component.text(value))

    constructor(value: Int) : this(Component.text(value))

    constructor(value: Boolean) : this(Component.text(value))

    companion object {
        fun t(key: String, vararg args: Textable) = Text(Component.translatable(key, *args))

        fun build(action: Builder.() -> Unit): Text {
            val b = Builder()
            b.action()
            return b.text
        }

        fun header(title: Textable) = Text("----------{ ").gold() + title + Text(" }----------").gold()

        fun list(items: Iterable<Textable>, sep: Textable = Text(", ").gray()) = build {
            val iter = items.iterator()
            while (iter.hasNext()) {
                +iter.next()
                if (iter.hasNext()) {
                    +sep
                }
            }
        }

        fun yesNo(value: Boolean) = if (value) t("nations.yes").darkGreen() else t("nations.no").darkRed()

        fun nationZone(nation: Textable, zone: Textable, clickable: Boolean = false) = build {
            +nation.toText(clickable)
            +Text(" - ").gray()
            +zone.toText(clickable)
        }
    }

    override fun toText(clickable: Boolean) = this

    operator fun plus(text: Textable) = Text(component.append(text.toText().component))

    operator fun plus(str: String) = Text(component.append(Component.text(str)))

    private fun color(color: TextColor) = Text(component.color(color))
    fun black() = color(NamedTextColor.BLACK)
    fun darkBlue() = color(NamedTextColor.DARK_BLUE)
    fun darkGreen() = color(NamedTextColor.DARK_GREEN)
    fun darkAqua() = color(NamedTextColor.DARK_AQUA)
    fun darkRed() = color(NamedTextColor.DARK_RED)
    fun darkPurple() = color(NamedTextColor.DARK_PURPLE)
    fun gold() = color(NamedTextColor.GOLD)
    fun gray() = color(NamedTextColor.GRAY)
    fun darkGray() = color(NamedTextColor.DARK_GRAY)
    fun blue() = color(NamedTextColor.BLUE)
    fun green() = color(NamedTextColor.GREEN)
    fun aqua() = color(NamedTextColor.AQUA)
    fun red() = color(NamedTextColor.RED)
    fun lightPurple() = color(NamedTextColor.LIGHT_PURPLE)
    fun yellow() = color(NamedTextColor.YELLOW)
    fun white() = color(NamedTextColor.WHITE)

    private fun decorate(decoration: TextDecoration) = Text(component.decorate(decoration))
    fun bold() = decorate(TextDecoration.BOLD)
    fun italic() = decorate(TextDecoration.ITALIC)
    fun obfuscated() = decorate(TextDecoration.OBFUSCATED)
    fun strikethrough() = decorate(TextDecoration.STRIKETHROUGH)
    fun underlined() = decorate(TextDecoration.UNDERLINED)

    private fun clickEvent(event: ClickEvent) = Text(component.clickEvent(event))
    fun runCommand(command: String) = clickEvent(ClickEvent.runCommand(command))

    fun hover(text: Textable) = Text(component.hoverEvent(text.asComponent()))

    enum class Format {
        JSON,
        LEGACY
    }

    fun serialize(format: Format) = when (format) {
        Format.JSON -> JSONComponentSerializer.json().serialize(component)
        Format.LEGACY -> LegacyComponentSerializer.legacySection().serialize(component)
    }

    class Builder {
        var text = Text()
            private set

        operator fun Textable.unaryPlus() {
            text += this
        }

        operator fun String.unaryPlus() {
            text += this
        }
    }
}
