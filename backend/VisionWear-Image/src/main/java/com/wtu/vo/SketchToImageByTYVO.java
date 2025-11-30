package com.wtu.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 通义万象线稿成图响应VO
 */
@Data
@Builder
public class SketchToImageByTYVO {
    private String requestId;
    private List<GeneratedImage> images;
    private String prompt;
    private String baseImageUrl;
    private long generationTimeMs;

    @Data
    @Builder
    public static class GeneratedImage {
        private String imageId;
        private String imageUrl;
        private int width;
        private int height;
        private long seed;
    }
}


