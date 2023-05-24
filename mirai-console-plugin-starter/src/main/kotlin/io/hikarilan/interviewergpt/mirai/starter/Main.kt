package io.hikarilan.interviewergpt.mirai.starter

import io.hikarilan.interviewergpt.mirai.PluginMain
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader
import net.mamoe.mirai.console.util.ConsoleExperimentalApi

object Main {

    @OptIn(ConsoleExperimentalApi::class)
    @JvmStatic
    fun main(args: Array<String>) {
        MiraiConsoleTerminalLoader.startAsDaemon()

        PluginMain.load()
        PluginMain.enable()

        runBlocking {
            MiraiConsole.job.join()
        }
    }

}