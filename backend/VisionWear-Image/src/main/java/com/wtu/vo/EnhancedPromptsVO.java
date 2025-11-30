package com.wtu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 增强提示词返回对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "增强提示词返回对象")
public class EnhancedPromptsVO {
    
    @Schema(description = "原始服装名称")
    private String original;
    
    @Schema(description = "格式化的推荐提示词")
    private String formatted;
    
    @Schema(description = "随机生成的提示词列表")
    private List<String> randomPrompts;
    
    @Schema(description = "服装特征信息")
    private Map<String, List<String>> features;
}

