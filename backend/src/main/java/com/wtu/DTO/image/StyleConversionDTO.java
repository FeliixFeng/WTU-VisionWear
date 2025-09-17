package com.wtu.dto.image;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaochen
 * @description: 风格转换DTO
 * @date 2025/6/25 15:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "风格转换请求参数")
public class StyleConversionDTO {

    @Schema(description = "图片Base64编码（二选一，优先生效）")
    @NotBlank(message = "图片Base64编码不难为空")
    private String imageBase64;

    @Schema(description = "漫画类型，可选值：jzcartoon（剪纸风）、watercolor_cartoon（水彩风）",
            defaultValue = "jzcartoon")
    @NotBlank(message = "漫画类型不能为空")
    private String type;
}
