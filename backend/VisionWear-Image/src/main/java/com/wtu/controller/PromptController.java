package com.wtu.controller;

import com.wtu.dto.prompt.PromptGenerateDTO;
import com.wtu.vo.EnhancedPromptsVO;
import com.wtu.result.Result;
import com.wtu.service.Neo4jPromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 提示词生成控制器
 */
@RestController
@RequestMapping("/api/prompt")
@RequiredArgsConstructor
@Tag(name = "提示词生成模块")
@Slf4j
public class PromptController {

    private final Neo4jPromptService neo4jPromptService;

    @Operation(summary = "生成增强提示词")
    @PostMapping("/generate")
    public Result<EnhancedPromptsVO> generateEnhancedPrompts(@Valid @RequestBody PromptGenerateDTO dto) {
        try {
            EnhancedPromptsVO result = neo4jPromptService.getEnhancedPrompts(
                    dto.getGarmentName(), 
                    dto.getOccasion(), 
                    dto.getNumPrompts()
            );
            return Result.success(result, "提示词生成成功");
        } catch (Exception e) {
            log.error("生成提示词失败", e);
            return Result.error("生成提示词失败: " + e.getMessage());
        }
    }

    @Operation(summary = "测试Neo4j连接")
    @GetMapping("/test-connection")
    public Result<String> testConnection() {
        try {
            boolean connected = neo4jPromptService.testConnection();
            if (connected) {
                return Result.success("连接成功", "Neo4j数据库连接正常");
            } else {
                return Result.error("Neo4j数据库连接失败");
            }
        } catch (Exception e) {
            log.error("测试Neo4j连接失败", e);
            return Result.error("连接测试失败: " + e.getMessage());
        }
    }
}
