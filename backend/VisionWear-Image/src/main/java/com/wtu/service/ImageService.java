package com.wtu.service;
import com.wtu.dto.image.*;
import com.wtu.vo.ImageFusionVO;
import com.wtu.vo.DoodleToImageByTYVO;
import com.wtu.vo.SketchToImageByTYVO;
import com.wtu.vo.SketchToImageVO;
import java.util.List;

/**
 * @author 高琛
 */
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
     * 通义万象文生图功能（SDK异步）
     *
     * @param request 文生图请求
     * @param userId  用户ID
     * @return 文生图响应
     */
    List<String> textToImageByTongyi(TextToImageByTYDTO request, Long userId) throws Exception;


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
     * 通义万象涂鸦生图功能（SDK异步）
     *
     * @param request 涂鸦生图请求
     * @param userId  用户ID
     * @return 涂鸦生图响应
     */
    DoodleToImageByTYVO DoodleToImageByTongyi(DoodleToImageByTYDTO request, Long userId) throws Exception;


    /**
     * 通义万象线稿成图功能（使用wanx2.1-imageedit模型）
     *
     * @param request 线稿成图请求
     * @param userId  用户ID
     * @return 线稿成图响应
     */
    SketchToImageByTYVO sketchToImageByTongyi(SketchToImageByTYDTO request, Long userId) throws Exception;

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

    /**
     * 豆包模型风格转换功能
     *
     * @param request 请求参数
     * @param userId  用户id
     * @return 图像URL列表
     */
    String styleConversion(StyleConversionDTO request, Long userId) throws Exception;
    /**
     *获取图片融合的进度
     * @param jobId 任务ID
     * @param userId  用户id
     * @return 生成进度
     */
    String getFusionProgress(String jobId, Long userId);
}
