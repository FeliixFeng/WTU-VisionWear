package com.wtu.vo;

import lombok.*;

import java.util.List;

/**
 * 线稿生图
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SketchToImageVO {
    private String requestId;        // 请求唯一ID
    private String taskId;           // 异步任务ID
    private String taskStatus;       // 任务状态（如SUCCEEDED/FAILED/RUNNING）
    private List<GeneratedImage> images; // 生成图片列表
    private Integer total;           // 总任务数
    private Integer succeeded;       // 成功数
    private Integer failed;          // 失败数
    private Long generationTimeMs;   // 生成耗时，可选

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneratedImage {
        private String imageId;      // 本地/OSS图片ID（如有）
        private String imageUrl;     // 结果图片URL
    }
}