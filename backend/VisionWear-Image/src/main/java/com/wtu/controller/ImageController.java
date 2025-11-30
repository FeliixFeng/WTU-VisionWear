package com.wtu.controller;
import com.wtu.dto.image.*;
import com.wtu.vo.DoodleToImageByTYVO;
import com.wtu.vo.ImageFusionVO;
import com.wtu.vo.SketchToImageByTYVO;
import com.wtu.vo.SketchToImageVO;
import com.wtu.exception.BusinessException;
import com.wtu.result.Result;
import com.wtu.service.ImageService;
import com.wtu.service.ImageStorageService;
import com.wtu.utils.AliOssUtil;
import com.wtu.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public Result<List<String>> textToImage(@RequestBody @Valid TextToImageDTO request) {
        try {
            // 从ThreadLocal获取当前用户ID
            Long userId = UserContext.getCurrentUserId();
            log.info("当前用户ID: {}", userId);
            // 调用用户服务的textToImage方法生成图像
            List<String> ids = imageService.textToImage(request, userId);
            List<String> urls = ids.stream().map(imageStorageService::getImageUrl).collect(Collectors.toList());
            return Result.success(urls);
        } catch (Exception e) {
            throw new BusinessException("文生图失败: " + e.getMessage());
        }
    }


    @PostMapping("/tongyi/text-to-image")
    @Operation(summary = "通义万象文生图功能")
    public Result<List<String>> textToImageByTongyi(@RequestBody @Valid TextToImageByTYDTO request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            List<String> ids = imageService.textToImageByTongyi(request, userId);
            List<String> urls = ids.stream().map(imageStorageService::getImageUrl).collect(Collectors.toList());
            return Result.success(urls);
        } catch (Exception e) {
            log.error("通义万象文生图失败", e);
            throw new BusinessException("通义万象文生图失败: " + e.getMessage());
        }
    }

    @PostMapping("/doubao/image-to-image")
    @Operation(summary = "图生图功能")
    public Result<List<String>> imageToImage(@RequestBody ImageToImageDTO request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            // 调用用户服务的imageToImage方法生成图像
            List<String> ids = imageService.imageToImage(request, userId);
            List<String> urls = ids.
                    stream().
                    map(imageStorageService::getImageUrl).
                    collect(Collectors.toList());

            return Result.success(urls);
        } catch (Exception e) {
            // 错误信息处理保持不变
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("520")) {
                throw new BusinessException("调用Stable Diffusion API时发生服务器错误(520)，请检查API配置和请求参数");
            }
            throw new BusinessException("以图生图失败: " + e.getMessage());
        }
    }

    @PostMapping("/doubao/styleConversion")
    @Operation(summary = "图片风格转换功能")
    public Result<String> styleConversion(@RequestBody StyleConversionDTO request) {
        try {
            //service层处理id与URL转换，Controller层直接返回即可.
            return Result.success(imageService.styleConversion(request,
                    UserContext.getCurrentUserId()));
        } catch (Exception e) {
            throw new BusinessException("图片风格转换失败: " + e.getMessage());
        }
    }

    @PostMapping("/image-fusion")
    @Operation(summary = "图片融合功能")
    public Result<ImageFusionVO> imageFusion(@RequestBody @Valid ImageFusionDTO request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            ImageFusionVO response = imageService.imageFusion(request, userId);

            return Result.success(response);
        } catch (Exception e) {
            throw new BusinessException("图片融合失败: " + e.getMessage());
        }
    }

    @GetMapping("/image-fusion/progress")
    @Operation(summary = "获取图片融合进度")
    public Result<String> getFusionProgress(@RequestParam String jobId ) {
            Long userId = UserContext.getCurrentUserId();
            return Result.success(imageService.getFusionProgress(jobId, userId));
    }

    @GetMapping("/image-fusion/result")
    @Operation(summary = "获取图片融合结果")
    public Result<ImageFusionVO> getFusionResult(@RequestParam String jobId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            ImageFusionVO response = imageService.queryImageByJobId(jobId, userId);
            return Result.success(response);
        } catch (Exception e) {
            throw new BusinessException("获取融合结果失败: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "通义万象涂鸦作图功能")
    public Result<DoodleToImageByTYVO> DoodleToImageByTongyi(
            @RequestBody @Valid DoodleToImageByTYDTO request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            DoodleToImageByTYVO vo = imageService.DoodleToImageByTongyi(request, userId);
            return Result.success(vo);
        } catch (Exception e) {
            throw new BusinessException("通义万象涂鸦作图失败: " + e.getMessage());
        }
    }

    @PostMapping("/sketch-to-image")
    @Operation(summary = "线稿生图功能")
    public Result<List<String>> sketchToImage(@RequestBody @Validated SketchToImageDTO request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            SketchToImageVO response = imageService.sketchToImage(request, userId);
            List<String> ids = response.getImages().stream()
                    .map(SketchToImageVO.GeneratedImage::getImageId)
                    .collect(Collectors.toList());

            List<String> urls = ids.
                    stream().
                    map(imageStorageService::getImageUrl).
                    collect(Collectors.toList());
            return Result.success(urls);
        } catch (Exception e) {
            throw new BusinessException("线稿生图失败: " + e.getMessage());
        }
    }


    @PostMapping("/tongyi/sketch-to-image")
    @Operation(summary = "通义万象线稿成图功能")
    public Result<SketchToImageByTYVO> sketchToImageByTongyi(
            @RequestBody @Valid SketchToImageByTYDTO request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            SketchToImageByTYVO vo = imageService.sketchToImageByTongyi(request, userId);
            return Result.success(vo);
        } catch (Exception e) {
            log.error("通义万象线稿成图失败", e);
            throw new BusinessException("通义万象线稿成图失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    @Operation(description = "文件上传")
    public Result<String> upload(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new BusinessException("文件名为空");
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 构建新的文件名称
            String objectName = UUID.randomUUID() + extension;

            String filePath = aliOssUtil.upload(file.getBytes(), objectName);

            return Result.success(filePath);
        } catch (IOException e) {
            throw new BusinessException("文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            throw new BusinessException("上传过程发生错误: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(description = "获取所有图片URL")
    public Result<List<String>> getAllImageUrls(@RequestHeader Long userId){
        return Result.success(imageService.getAllImageUrls(userId));
    }


}
