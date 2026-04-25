package icu.ruiyu.framework.integration.llm.service;

import dev.langchain4j.data.message.ChatMessage;

import java.util.List;

/**
 * LLM 服务统一接口
 */
public interface LlmService {

    /**
     * 获取 Provider 名称
     */
    String getProvider();

    /**
     * 同步对话
     *
     * @param userMessage 用户消息
     * @return AI 响应
     */
    String chat(String userMessage);

    /**
     * 对话（带聊天历史）
     *
     * @param messages 聊天历史
     * @return AI 响应
     */
    String chat(List<ChatMessage> messages);

    /**
     * 获取可用模型列表
     */
    List<String> getAvailableModels();
}
