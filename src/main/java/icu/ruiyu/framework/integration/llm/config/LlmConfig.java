package icu.ruiyu.framework.integration.llm.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * LangChain4j 配置
 */
@Configuration
public class LlmConfig {

    @Value("${langchain4j.open-ai.api-key:}")
    private String openAiApiKey;

    @Value("${langchain4j.open-ai.model-name:${OPENAI_MODEL_NAME:gpt-4o}}")
    private String openAiModelName;

    @Value("${langchain4j.open-ai.base-url:${OPENAI_BASE_URL:https://api.openai.com/v1}}")
    private String openAiBaseUrl;

    @Bean
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName(openAiModelName)
                .baseUrl(openAiBaseUrl)
                .timeout(Duration.ofSeconds(60))
                .build();
    }
}
