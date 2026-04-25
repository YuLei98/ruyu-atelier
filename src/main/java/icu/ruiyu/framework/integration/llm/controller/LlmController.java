package icu.ruiyu.framework.integration.llm.controller;

import icu.ruiyu.framework.common.CommonResult;
import icu.ruiyu.framework.integration.llm.service.impl.OpenAiLlmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * LLM 控制器
 */
@Slf4j
@RestController
@RequestMapping("/llm")
@Tag(name = "LLM", description = "大模型调用接口")
public class LlmController {

    @Autowired
    private OpenAiLlmService openAiLlmService;

    /**
     * 对话
     */
    @PostMapping("/chat")
    @Operation(summary = "对话", description = "调用大模型进行对话")
    public CommonResult<String> chat(
            @Parameter(description = "用户消息")
            @RequestBody Map<String, String> request) {

        String message = request.get("message");
        try {
            String response = openAiLlmService.chat(message);
            return CommonResult.success(response);
        } catch (Exception e) {
            log.error("LLM chat error", e);
            return CommonResult.fail(e.getMessage());
        }
    }

    /**
     * 获取可用模型列表
     */
    @GetMapping("/models")
    @Operation(summary = "获取可用模型", description = "获取可用模型列表")
    public CommonResult<List<String>> getModels() {
        return CommonResult.success(openAiLlmService.getAvailableModels());
    }
}
