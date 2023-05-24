package io.hikarilan.interviewergpt

import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost

interface Interview {

    val isStarted: Boolean
    val isEnded: Boolean

    fun start(): String?

    fun answer(answer: String): String

    fun end(): String?

}

fun Interview(client: OpenAI, modelId: ModelId): OpenAIInterview {
    return OpenAIInterview(client, modelId)
}

fun Interview(client: OpenAI): OpenAIInterview {
    return OpenAIInterview(client, ModelId("gpt-3.5-turbo"))
}

fun Interview(): OpenAIInterview {
    return OpenAIInterview(
        OpenAI(token = System.getProperty("OPENAI-TOKEN")
            ?: throw IllegalStateException("OPENAI-TOKEN is not set."),
            host = System.getProperty("OPENAI-HOST")?.let { OpenAIHost(it) } ?: OpenAIHost.OpenAI),
        ModelId("gpt-3.5-turbo")
    )
}