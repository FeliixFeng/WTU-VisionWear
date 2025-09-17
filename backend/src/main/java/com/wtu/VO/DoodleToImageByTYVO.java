package com.wtu.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 通义万象涂鸦作图响应VO
 */
@Data
@Builder
public class DoodleToImageByTYVO {
    private String requestId;
    private List<GeneratedImage> images;
    private String prompt;
    private String sketchImageUrl;
    private long generationTimeMs;

    @Data
    @Builder
    public static class GeneratedImage {
        private String imageId;
        // 可以移除base64Image字段，减少传输数据量
        // private String base64Image;
        private String imageUrl;
        private int width;
        private int height;
        private long seed;
        private String style;
    }
}
