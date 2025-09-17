package com.wtu.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/04/13/21:00
 * @Description: 文生图功能请求参数封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextToImageDTO {
    @Schema(description = "算法名称，固定为high_aes_general_v30l_zt2i",defaultValue = "high_aes_general_v30l_zt2i")
    @NotBlank(message = "reqKey为必填项")
    @JsonProperty("req_key")
    private String reqKey="high_aes_general_v30l_zt2i";

    @Schema(description = "用于生成图像的提示词，中英文均可", example = "一只可爱的猫")
    @NotBlank(message = "prompt为必填项")
    private String prompt;

    @Schema(description = "开启文本扩写，建议prompt较短时开启，默认false", defaultValue = "false")
    @JsonProperty("use_pre_llm")
    private Boolean usePreLlm = false;

    @Schema(description = "随机种子，默认-1（随机）", defaultValue = "-1")
    private Integer seed = -1;

    @Schema(description = "影响文本描述的程度，默认2.5，范围[1,10]", defaultValue = "2.5")
    @DecimalMin(value = "1.0", message = "scale不能小于1")
    @DecimalMax(value = "10.0", message = "scale不能大于10")
    private Float scale = 2.5f;

    @Schema(description = "生成图像的宽，默认1328，范围[512,2048]", defaultValue = "1328")
    @Min(value = 512, message = "width不能小于512")
    @Max(value = 2048, message = "width不能大于2048")
    private Integer width = 1328;

    @Schema(description = "生成图像的高，默认1328，范围[512,2048]", defaultValue = "1328", example = "1328")
    @Min(value = 512, message = "height不能小于512")
    @Max(value = 2048, message = "height不能大于2048")
    private Integer height = 1328;


}
