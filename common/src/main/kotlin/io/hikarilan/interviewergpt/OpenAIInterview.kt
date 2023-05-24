package io.hikarilan.interviewergpt

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.runBlocking

@OptIn(BetaOpenAI::class)
class OpenAIInterview(private val client: OpenAI, private val modelId: ModelId) : Interview {

    override var isStarted = false
    override var isEnded = false

    private val context = mutableListOf<ChatMessage>()

    private var lastAskTime = System.currentTimeMillis()

    override fun start(): String {
        if (isStarted) throw IllegalStateException("Interview has already started.")
        if (isEnded) throw IllegalStateException("Interview has already ended.")

        isStarted = true

        context.add(ChatMessage(role = ChatRole.System, content = Prompt.system))
        context.add(ChatMessage(role = ChatRole.User, content = Prompt.onStart))

        return runBlocking {
            requestChatCompletion()
        }
    }

    override fun answer(answer: String): String {
        if (Prompt.endRegex.toRegex().containsMatchIn(answer)) return end()

        if (!isStarted) throw IllegalStateException("Interview has not started yet.")
        if (isEnded) throw IllegalStateException("Interview has already ended.")

        context.add(ChatMessage(role = ChatRole.User, content = Prompt.answerTemplate.format(calcAnswerTime(), answer)))

        return runBlocking {
            requestChatCompletion()
        }
    }

    override fun end(): String {
        if (!isStarted) throw IllegalStateException("Interview has not started yet.")
        if (isEnded) throw IllegalStateException("Interview has already ended.")

        isEnded = true

        context.add(ChatMessage(role = ChatRole.User, content = Prompt.onEnd))

        return runBlocking {
            requestChatCompletion()
        }
    }

    private suspend fun requestChatCompletion(): String {
        return client.chatCompletion(
            ChatCompletionRequest(
                model = modelId,
                messages = context,
                temperature = 0.5,
                maxTokens = 2048
            )
        ).choices.sortedBy { it.index }.map { it.message?.content }.joinToString(separator = " ") {
            it ?: ""
        }.also { context.add(ChatMessage(role = ChatRole.Assistant, content = it)) }.also { refreshAskTime() }
    }

    private fun refreshAskTime() {
        lastAskTime = System.currentTimeMillis()
    }

    private fun calcAnswerTime(): Long {
        return (System.currentTimeMillis() - lastAskTime) / 1000
    }

}