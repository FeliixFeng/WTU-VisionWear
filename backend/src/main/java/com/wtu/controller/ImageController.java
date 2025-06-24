package com.wtu.controller;
import com.wtu.DTO.ImageFusionDTO;
import com.wtu.DTO.ImageToImageDTO;
import com.wtu.DTO.SketchToImageDTO;
import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.ImageFusionVO;
import com.wtu.VO.SketchToImageVO;
import com.wtu.result.Result;
import com.wtu.service.ImageService;
import com.wtu.service.ImageStorageService;
import com.wtu.utils.AliOssUtil;
import com.wtu.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/image")
@Tag(name = "图像生成模块")
@Slf4j
@RequiredArgsConstructor
public class ImageController {

    // IOC 注入
    private final ImageService imageService;
    private final ImageStorageService imageStorageService;
    private final AliOssUtil aliOssUtil;


    @PostMapping("/doubao/text-to-image")
    @Operation(summary = "文生图功能")
    public Result<List<String>> textToImage(@RequestBody @Valid TextToImageDTO request,
                                            HttpServletRequest httpServletRequest) throws Exception {
        //从token 获取当前用户ID
        Long userId = UserContext.getCurrentUserId(httpServletRequest);
        // 调用用户服务的textToImage方法生成图像
        List<String> ids = imageService.textToImage(request, userId);
        List<String> urls = ids.stream().map(imageStorageService::getImageUrl).collect(Collectors.toList());
        log.info("URLS: {}", urls);
        return Result.success(urls);
    }

    @PostMapping("/doubao/image-to-image")
    @Operation(summary = "图生图功能")
    public Result<List<String>> imageToImage(@RequestBody ImageToImageDTO request,
                                             HttpServletRequest httpServletRequest) {
        try {
            Long userId = UserContext.getCurrentUserId(httpServletRequest);
            log.info("当前用户 ID: {}", userId);
            log.info("开始处理以图生图请求: {}", request);
            // 调用用户服务的imageToImage方法生成图像
            List<String> ids = imageService.imageToImage(request, userId);
            log.info("ids:{}",ids);
            List<String> urls = ids.
                    stream().
                    map(imageStorageService::getImageUrl).
                    collect(Collectors.toList());

            log.info("URLS: {}", urls);

            return Result.success(urls);
        } catch (Exception e) {
            log.error("以图生图失败", e);
            // 错误信息处理保持不变
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("520")) {
                return Result.error("调用Stable Diffusion API时发生服务器错误(520)，请检查API配置和请求参数");
            }
            return Result.error("以图生图失败: " + e.getMessage());
        }
    }

    @PostMapping("/image-fusion")
    @Operation(summary = "图片融合功能")
    public Result<ImageFusionVO> imageFusion(@RequestBody @Valid ImageFusionDTO request, HttpServletRequest httpServletRequest) {
        try {
            Long userId = UserContext.getCurrentUserId(httpServletRequest);
            ImageFusionVO response = imageService.imageFusion(request, userId);

            return Result.success(response);
        } catch (Exception e) {
            log.error("图片融合失败", e);
            return Result.error("图片融合失败: " + e.getMessage());
        }
    }

    @GetMapping("/image-fusion/result")
    @Operation(summary = "获取图片融合结果")
    public Result<ImageFusionVO> getFusionResult(@RequestParam String jobId, HttpServletRequest httpServletRequest) throws Exception {
        Long userId = UserContext.getCurrentUserId(httpServletRequest);
        ImageFusionVO response = imageService.queryImageByJobId(jobId, userId);
        return Result.success(response);
    }


    @PostMapping("/sketch-to-image")
    @Operation(summary = "线稿生图功能")
    public Result<List<String>> sketchToImage(@RequestBody @Validated SketchToImageDTO request,
                                              HttpServletRequest httpServletRequest) {
        try {
            Long userId = UserContext.getCurrentUserId(httpServletRequest);
            SketchToImageVO response = imageService.sketchToImage(request, userId);
            List<String> ids = response.getImages().stream()
                    .map(SketchToImageVO.GeneratedImage::getImageId)
                    .collect(Collectors.toList());
            return Result.success(ids);
        } catch (Exception e) {
            return Result.error("线稿生图失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    @Operation(description = "文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传开始: 文件名={}, 大小={}bytes",
                file.getOriginalFilename(), file.getSize());

        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                log.error("文件名为空");
                return Result.error("文件名为空");
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 构建新的文件名称
            String objectName = UUID.randomUUID() + extension;
            log.info("生成的对象名称: {}", objectName);

            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            log.info("上传成功，返回的文件路径: {}", filePath);

            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败, 错误: {}", e.getMessage(), e);
            return Result.error("文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传过程发生未知错误", e);
            return Result.error("上传过程发生错误: " + e.getMessage());
        }
    }

}
