package com.arckenver.nations.bukkit.command

import com.arckenver.nations.bukkit.Nations
import com.arckenver.nations.bukkit.text.Text
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

abstract class Command(
    private val name: String,
) {
    private val aliases: MutableList<String> = mutableListOf()
    private var description: Text? = null
    private var parent: Command? = null
    private val children: MutableList<Command> = mutableListOf()
    private val arguments: MutableList<CommandArgument<*>> = mutableListOf()

    private val shortestAlias get() = aliases.minWithOrNull { a, b -> a.length.compareTo(b.length) } ?: name

    fun withAliases(vararg aliases: String): Command {
        this.aliases.addAll(aliases)
        return this
    }

    fun withDescription(description: Text): Command {
        this.description = description
        return this
    }

    fun withChild(child: Command): Command {
        child.parent = this
        this.children.add(child)
        return this
    }

    fun withArgument(arg: CommandArgument<*>): Command {
        this.arguments.add(arg)
        return this
    }

    fun register(plugin: JavaPlugin) {
        plugin.getCommand(name)!!.setExecutor(BukkitExecutor(this))
    }

    @Throws(CommandException::class)
    open fun execute(ctx: CommandContext) {
        Nations.sendMessage(ctx.sender, help())
    }

    private fun handle(ctx: CommandContext, args: Iterator<String>) {
        val cmdArgs = arguments.iterator()

        while (args.hasNext() && cmdArgs.hasNext()) {
            val arg = args.next()
            val cmdArg = cmdArgs.next()

            val value = cmdArg.parse(ctx, arg) ?: throw CommandException.t(
                "nations.invalid_argument",
                Text(arg)
            )
            ctx.setArgument(cmdArg, value)
        }

        if (!args.hasNext()) {
            if (cmdArgs.hasNext()) {
                val cmdArg = cmdArgs.next()
                if (!cmdArg.optional) {
                    throw CommandException.t(
                        "nations.missing_argument",
                        Text(cmdArg.name)
                    )
                }
            }

            execute(ctx)
            return
        }

        val arg = args.next()
        val child = children.find { it.name == arg } ?: throw CommandException.t(
            "nations.unknown_command",
            Text(arg)
        )

        return child.handle(ctx, args)
    }

    private fun tabComplete(ctx: CommandContext, args: Iterator<String>): List<String> {
        var lastArg = ""
        if (args.hasNext()) {
            lastArg = args.next()
        }

        for (cmdArg in arguments) {
            if (!args.hasNext()) {
                return cmdArg.tabComplete(ctx, lastArg)
            }
            if (args.hasNext()) {
                lastArg = args.next()
            }
        }

        val child = children.find { it.name == lastArg }
        if (child != null) {
            return child.tabComplete(ctx, args)
        }

        if (args.hasNext()) {
            return emptyList()
        }

        return children
            .filter { it.name.startsWith(lastArg) }
            .map { it.name }
    }

    protected fun help(): Text {
        return Text.build {
            +Text.header(usagePrefix().yellow())
            +"\n"

            if (children.isEmpty()) {
                +Text.t("nations.no_commands").gray()
                return@build
            }

            val queue = children.toMutableList()
            while (queue.isNotEmpty()) {
                val child = queue.removeFirst()
                queue.addAll(child.children)

                if (child.description != null) {
                    +child.usage()
                    if (queue.isNotEmpty()) {
                        +"\n"
                    }
                }
            }
        }
    }

    private fun usage() = Text.build {
        +usagePrefix().gold()
        for (arg in arguments) {
            if (arg.optional)
                +Text(" [${arg.name}]").gold()
            else
                +Text(" <${arg.name}>").gold()
        }
        description?.let {
            +Text(" - ").gray()
            +it.yellow()
        }
    }

    private fun usagePrefix() = Text("/" + commandChain().joinToString(" "))

    private fun commandChain(): List<String> =
        (parent?.commandChain() ?: emptyList()) + listOf(shortestAlias)

    private class BukkitExecutor(val cmd: Command) : CommandExecutor, TabCompleter {
        override fun onCommand(
            sender: CommandSender,
            command: org.bukkit.command.Command,
            label: String,
            args: Array<String>
        ): Boolean {
            val ctx = CommandContext(sender)

            if (args.size == 1 && (args[0] == "help" || args[0] == "?")) {
                Nations.sendMessage(sender, cmd.help())
                return true
            }

            try {
                cmd.handle(ctx, args.iterator())
            } catch (e: CommandException) {
                Nations.sendMessage(sender, e.msg.red())
            } catch (e: Exception) {
                e.printStackTrace()
                Nations.sendMessage(sender, Text.t("nations.unexpected_error").red())
            }

            return true
        }

        override fun onTabComplete(
            sender: CommandSender,
            command: org.bukkit.command.Command,
            alias: String,
            args: Array<String>
        ): List<String> {
            val ctx = CommandContext(sender)

            return cmd.tabComplete(ctx, args.iterator())
        }
    }
}
