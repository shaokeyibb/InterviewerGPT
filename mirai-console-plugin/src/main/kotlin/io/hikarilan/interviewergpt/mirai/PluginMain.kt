package io.hikarilan.interviewergpt.mirai

import io.hikarilan.interviewergpt.Interview
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.events.GroupEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.globalEventChannel

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "io.hikarilan.interviewergpt",
        name = "InterviewerGPT",
        version = "1.0.0",
    ) {
        author("HikariLan")
    }
) {

    private val interviews = mutableMapOf<Pair<Bot, User>, Interview>()

    override fun onEnable() {
        logger.info("InterviewerGPT has been successfully enabled.")

        this.globalEventChannel().subscribeAlways<MessageEvent> {
            handleMessage()
        }
    }

    private suspend fun MessageEvent.handleMessage() {
        val lastInterview = interviews[bot to sender]

        if (lastInterview == null || lastInterview.isEnded) {
            interviews[bot to sender] = Interview()
        }

        val interview = interviews[bot to sender]!!

        val sender = if (this is GroupEvent) this.group else this.sender

        if (this.message.contentEquals("/开始面试") && interview.isStarted) {
            sender.sendMessage("你已经开始一场面试啦！")
            return
        }

        if (this.message.contentEquals("/结束面试") && (!interview.isStarted || interview.isEnded)) {
            sender.sendMessage("你还没有开始面试呢！")
            return
        }

        if (this.message.contentEquals("/开始面试")) {
            launch {
                interview.start()?.let { sender.sendMessage(it) }
            }
            return
        }

        if (this.message.contentEquals("/结束面试")) {
            launch {
                interview.end()?.let { sender.sendMessage(it) }
            }
            return
        }

        if (interview.isStarted && !interview.isEnded) {
            launch {
                interview.answer(message.toString()).let { sender.sendMessage(it) }
            }
            return
        }
    }


}