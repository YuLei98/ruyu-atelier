package icu.ruiyu.framework.integration.llm.service.impl;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import icu.ruiyu.framework.integration.llm.service.LlmService;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * OpenAI LLM 服务实现
 */
@Slf4j
@Service
public class OpenAiLlmService implements LlmService {

    @Resource
    private OpenAiChatModel chatModel;

    @Override
    public String getProvider() {
        return "openai";
    }

    @Override
    public String chat(String userMessage) {
        log.debug("OpenAI chat request: {}", userMessage);
        String response = chatModel.chat(userMessage);

        log.debug("OpenAI chat response: {}", response);
        return response;
    }

    @Override
    public String chat(List<ChatMessage> messages) {
        log.debug("OpenAI chat request with history, message count: {}", messages.size());
        ChatResponse response = chatModel.chat(messages);
        log.debug("OpenAI chat response: {}", response);
        return response.aiMessage().text();
    }

    @Override
    public List<String> getAvailableModels() {
        return Arrays.asList(
                "doubao-seed-2.0-code",
                "doubao-seed-2.0-pro",
                "doubao-seed-2.0-lite",
                "doubao-seed-code",
                "minimax-latest", // （推荐写法，对应模型minimax-m2.7）
                "glm-5.1",
                "glm-4.7",
                "deepseek-v3.2",
                "kimi-k2.6",
                "kimi-k2.5"
        );
    }
}
