package com.wtu.dto.prompt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 提示词生成请求参数
 */
@Data
@Schema(description = "提示词生成请求参数")
public class PromptGenerateDTO {
    
    @Schema(description = "服装名称", example = "连衣裙")
    @NotBlank(message = "服装名称不能为空")
    private String garmentName;
    
    @Schema(description = "场合", example = "正式场合")
    @NotBlank(message = "场合不能为空")
    private String occasion;
    
    @Schema(description = "生成提示词数量", example = "3")
    @Min(value = 1, message = "生成数量至少为1")
    private Integer numPrompts = 3;
}

