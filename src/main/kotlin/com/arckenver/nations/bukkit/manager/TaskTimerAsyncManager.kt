package com.arckenver.nations.bukkit.manager

import org.bukkit.plugin.java.JavaPlugin

abstract class TaskTimerAsyncManager(
    val delay: Long,
    val period: Long,
) {
    abstract fun runTask()

    fun schedule(plugin: JavaPlugin) {
        plugin.server.scheduler.runTaskTimerAsynchronously(plugin, Runnable { runTask() }, delay, period)
    }
}
