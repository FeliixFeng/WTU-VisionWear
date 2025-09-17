package com.wtu.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/04/19/18:38
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageToImageVO {
    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 源图像url
     */
    private String sourceImageUrl;

    /**
     * 使用的提示词
     */
    private String prompt;

    /**
     * 生成的图像列表
     */
    private List<GeneratedImage> images;

    /**
     * 生成耗时(毫秒)
     */
    private long generationTimeMs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneratedImage {
        /**
         * 图像ID
         */
        private String imageId;

        /**
         * 图像URL
         */
        private String imageUrl;

        /**
         * 图像宽度
         */
        private int width;

        /**
         * 图像高度
         */
        private int height;

        /**
         * 生成种子
         */
        private long seed;
    }
}

