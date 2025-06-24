package com.wtu.service;


import com.wtu.DTO.ImageFusionDTO;
import com.wtu.DTO.ImageToImageDTO;
import com.wtu.DTO.SketchToImageDTO;
import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.ImageFusionVO;
import com.wtu.VO.SketchToImageVO;

import java.util.List;

public interface ImageService {
    /**
     * 文本生成图像 (Text-to-Image)
     *
     * @param request 图像生成请求
     * @param userId  用户ID
     * @return 图像生成响应
     */
    List<String> textToImage(TextToImageDTO request, Long userId) throws Exception;

    /**
     * 图像生成图像 (Image-to-Image)
     *
     * @param request 以图生图请求
     * @param userId  用户ID
     * @return 图像生成响应
     */
    List<String> imageToImage(ImageToImageDTO request, Long userId) throws Exception;

    /**
     * 线稿生成图像 (Sketch-to-Image)
     *
     * @param request 图像生成请求
     * @param userId  用户ID
     * @return 图像生成响应
     */
    SketchToImageVO sketchToImage(SketchToImageDTO request, Long userId) throws Exception;

    /**
     * 图像融合
     *
     * @param request 图像融合请求
     * @param userId  用户ID
     * @return 图像融合响应
     */
    ImageFusionVO imageFusion(ImageFusionDTO request, Long userId) throws Exception;

    /**
     * 通过jobId查询图像生成结果
     *
     * @param jobId  任务ID
     * @param userId 用户ID
     * @return 图像生成结果
     */
    ImageFusionVO queryImageByJobId(String jobId, Long userId) throws Exception;

    /**
     * 获取所有图像URL
     *
     * @param userId 用户ID
     * @return 图像URL列表
     */
    List<String> getAllImageUrls(Long userId);


}
