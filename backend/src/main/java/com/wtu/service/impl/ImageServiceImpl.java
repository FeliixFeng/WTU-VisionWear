package com.wtu.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.tencentcloudapi.aiart.v20221229.AiartClient;
import com.tencentcloudapi.aiart.v20221229.models.SketchToImageRequest;
import com.tencentcloudapi.aiart.v20221229.models.SketchToImageResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.model.request.ImageStyleConversionRequest;
import com.volcengine.service.visual.model.response.ImageStyleConversionResponse;
import com.wtu.DTO.image.*;
import com.wtu.VO.ImageFusionVO;
import com.wtu.VO.SketchToImageVO;
import com.wtu.entity.Image;
import com.wtu.exception.BusinessException;
import com.wtu.exception.ExceptionUtils;
import com.wtu.mapper.ImageMapper;
import com.wtu.service.ImageService;
import com.wtu.service.ImageStorageService;
import com.wtu.utils.ImageBase64Util;
import com.wtu.utils.ModelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    // IOC 注入
    private final RestTemplate restTemplate;
    private final ImageStorageService imageStorageService;
    private final ImageMapper imageMapper;
    private final ImageBase64Util imageBase64Util;

    // 文本生成图像
    @Override
    public List<String> textToImage(TextToImageDTO request, Long userId) throws Exception {
        ExceptionUtils.requireNonNull(request, "请求参数不能为空");
        ExceptionUtils.requireNonNull(request.getPrompt(), "生成提示词不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");
        
        try {
            List<String> ids = new ArrayList<>();
            //用工具类快速构造VisualService
            IVisualService visualService = ModelUtils.createVisualService("cn-north-1");
            //用工具类转为JsonObject类型
            JSONObject req = ModelUtils.toJsonObject(request);
            //如果用户prompt字数过少，则开启自动文本优化
            if (request.getPrompt().length() < 10) {
                req.put("use_pre_llm", true);
            }
            //发送请求，得到response
            Object response = visualService.cvProcess(req);
            //从response中拿出base64编码
            List<String> base64Array = ModelUtils.getBase64(response);
            
            if (base64Array == null || base64Array.isEmpty()) {
                throw new BusinessException("未能生成有效的图像");
            }
            
            for (String s : base64Array) {
                ids.add(imageStorageService.saveBase64Image(s, userId));
            }

            return ids;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("文本生成图像失败", e);
            throw new BusinessException("文本生成图像失败: " + e.getMessage());
        }
    }

    // 图像生成图像
    @Override
    public List<String> imageToImage(ImageToImageDTO request, Long userId) throws Exception {
        ExceptionUtils.requireNonNull(request, "请求参数不能为空");
        ExceptionUtils.requireNonNull(request.getReqKey(), "请求Key不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");

        try {
            List<String> ids = new ArrayList<>();
            JSONObject jsonRequest = ModelUtils.toJsonObject(request);
            //用工具类构造IvisualService实例
            IVisualService visualService = ModelUtils.createVisualService("cn-north-1");

            JSONObject response = (JSONObject) visualService.cvSync2AsyncSubmitTask(jsonRequest);

            //这个方法，需要传的是一整个未被剪切的JsonObject，方法会自动帮我们剪切出data，然后从data中拿取我们想要的数据。
            String taskId = ModelUtils.getFromJsonData(response, "task_id", String.class);
            if (taskId == null || taskId.isEmpty()) {
                throw new BusinessException("获取任务ID失败");
            }
            
            //用taskID去申请另一个接口，得到图片
            JSONObject taskRequest = new JSONObject();
            taskRequest.put("req_key", request.getReqKey());
            taskRequest.put("task_id", taskId);

            // 设置最大重试次数和超时时间
            int maxRetries = 30; // 最多重试30次
            int retryCount = 0;
            long sleepTime = 2000; // 初始等待2秒

            while (retryCount < maxRetries) {
                //不断循环请求结果，直到状态为Done，再拿取base64
                JSONObject taskResponse = (JSONObject) visualService.cvSync2AsyncGetResult(taskRequest);
                String status = ModelUtils.getFromJsonData(taskResponse, "status", String.class);
                if ("done".equals(status)) {
                    //先确保taskResponse有数据
                    List<String> base64 = ModelUtils.getBase64(taskResponse);
                    if (base64 == null || base64.isEmpty()) {
                        throw new BusinessException("获取图像结果失败");
                    }
                    
                    for (String s : base64) {
                        //对base64进行解码并上传,得到ImageID,存入ids中
                        ids.add(imageStorageService.saveBase64Image(s, userId));
                    }
                    return ids;
                } else if ("failed".equals(status)) {
                    throw new BusinessException("图像生成任务失败");
                }
                
                // 等待一段时间后重试
                Thread.sleep(sleepTime);
                retryCount++;
                // 逐步增加等待时间，但最多等待10秒
                sleepTime = Math.min(sleepTime * 2, 10000);
            }
            
            throw new BusinessException("图像生成超时，请稍后重试");
        } catch (BusinessException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("图像生成被中断");
        } catch (Exception e) {
            log.error("图像生成图像失败", e);
            throw new BusinessException("图像生成失败: " + e.getMessage());
        }
    }

    // 线稿生图
    // 根据需求调整地域
    private static final String TENCENT_REGION = "ap-shanghai";

    @Value("${vision.tencent.secret-id}")
    private String tencentSecretId;

    @Value("${vision.tencent.secret-key}")
    private String tencentSecretKey;

    @Override
    public SketchToImageVO sketchToImage(SketchToImageDTO request, Long userId) throws Exception {
        ExceptionUtils.requireNonNull(request, "请求参数不能为空");
        ExceptionUtils.requireNonNull(request.getSketchImageId(), "线稿图ID不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");
        
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();

        try {
            // 1. 获取线稿图URL
            String sketchUrl = request.getSketchImageId();
            if (!sketchUrl.startsWith("http")) {
                sketchUrl = imageStorageService.getImageUrl(sketchUrl);
            }
            log.info("线稿图URL: {}", sketchUrl);

            // 2. 使用腾讯云SDK调用API
            SketchToImageResponse response = callTencentSketchToImage(
                    sketchUrl,
                    request.getPrompt(),
                    request.getRspImgType()
            );

            // 3. 解析响应并保存图片
            String resultImageUrl = response.getResultImage();
            if (resultImageUrl == null || resultImageUrl.isEmpty()) {
                throw new BusinessException("获取结果图像URL失败");
            }
            
            String imageId = imageStorageService.saveImageFromUrl(resultImageUrl, userId);

            return SketchToImageVO.builder()
                    .requestId(requestId)
                    .sketchImageId(request.getSketchImageId())
                    .images(List.of(
                            SketchToImageVO.GeneratedImage.builder()
                                    .imageId(imageId)
                                    .imageUrl(imageStorageService.getImageUrl(imageId))
                                    .build()
                    ))
                    .generationTimeMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (TencentCloudSDKException e) {
            log.error("腾讯云API调用失败: {}", e.getMessage());
            throw new BusinessException("线稿生图服务暂不可用: " + e.getMessage());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("线稿生图失败", e);
            throw new BusinessException("线稿生图失败: " + e.getMessage());
        }
    }

    @Value("${vision.ttapi.api-key}")
    private String ttApiKey;

    @Override
    public ImageFusionVO imageFusion(ImageFusionDTO request, Long userId) {
        ExceptionUtils.requireNonNull(request, "请求参数不能为空");
        ExceptionUtils.requireNonNull(request.getImageUrlList(), "图片URL列表不能为空");
        ExceptionUtils.requireTrue(!request.getImageUrlList().isEmpty(), "图片URL列表不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");
        
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        
        try {
            log.info("开始图片融合请求: {}", requestId);
            
            // 1. 图片URL转Base64
            List<String> base64Images = request.getImageUrlList().stream()
                    .map(imageBase64Util::imageUrlToBase64)
                    .collect(Collectors.toList());

            // 2. 组装请求体
            Map<String, Object> body = new HashMap<>();
            body.put("imgBase64Array", base64Images);
            body.put("dimensions", request.getDimensions());
            body.put("mode", request.getMode());
            body.put("hookUrl", request.getHookUrl());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("TT-API-KEY", ttApiKey);

            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);

            // 3. 调用blend接口提交任务
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                    "https://api.ttapi.io/midjourney/v1/blend",
                    httpEntity,
                    JsonNode.class);

            JsonNode responseJson = responseEntity.getBody();
            if (responseJson == null || !"SUCCESS".equals(responseJson.path("status").asText())) {
                throw new BusinessException("图片融合失败: " + (responseJson != null ? responseJson.path("message").asText() : "无响应"));
            }

            String jobId = responseJson.path("data").path("jobId").asText();
            if (jobId == null || jobId.isEmpty()) {
                throw new BusinessException("获取任务ID失败");
            }
            
            log.info("图片融合任务提交成功，jobId={}", jobId);

            // 4. 只返回jobId，前端或调用方后续用jobId查询结果
            return ImageFusionVO.builder()
                    .requestId(requestId)
                    .jobId(jobId)
                    .generationTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("图片融合失败", e);
            throw new BusinessException("图片融合失败: " + e.getMessage());
        }
    }

    @Override
    public ImageFusionVO queryImageByJobId(String jobId, Long userId) {
        ExceptionUtils.requireNonEmpty(jobId, "任务ID不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");
        
        try {
            log.info("查询图片融合结果，jobId: {}, userId: {}", jobId, userId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("TT-API-KEY", ttApiKey);

            Map<String, String> body = new HashMap<>();
            body.put("jobId", jobId);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    "https://api.ttapi.io/midjourney/v1/fetch", 
                    request, 
                    JsonNode.class
            );
            
            JsonNode responseJson = response.getBody();
            if (responseJson == null) {
                throw new BusinessException("无响应");
            }

            String status = responseJson.path("status").asText();
            if (!"SUCCESS".equals(status)) {
                String msg = responseJson.path("message").asText();
                throw new BusinessException("查询失败: " + msg);
            }

            JsonNode dataNode = responseJson.path("data");
            String cdnImage = dataNode.path("cdnImage").asText(null);
            if (cdnImage == null || cdnImage.isEmpty()) {
                throw new BusinessException("未获取到图片地址");
            }

            String imageId = imageStorageService.saveImageFromUrl(cdnImage, userId);
            String ossImageUrl = imageStorageService.getImageUrl(imageId);

            ImageFusionVO.GeneratedImage generatedImage = ImageFusionVO.GeneratedImage.builder()
                    .imageId(imageId)
                    .imageUrl(ossImageUrl)
                    .build();

            return ImageFusionVO.builder()
                    .requestId(UUID.randomUUID().toString())
                    .images(Collections.singletonList(generatedImage))
                    .generationTimeMs(0)
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询图片融合结果失败", e);
            throw new BusinessException("查询图片融合结果失败: " + e.getMessage());
        }
    }

    private SketchToImageResponse callTencentSketchToImage(
            String sketchUrl,
            String prompt,
            String rspImgType
    ) throws TencentCloudSDKException {
        ExceptionUtils.requireNonEmpty(sketchUrl, "线稿图URL不能为空");
        
        // 配置认证信息
        Credential cred = new Credential(tencentSecretId, tencentSecretKey);

        // 创建客户端
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("aiart.tencentcloudapi.com");
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        AiartClient client = new AiartClient(cred, TENCENT_REGION, clientProfile);

        // 构建请求
        SketchToImageRequest req = new SketchToImageRequest();
        req.setInputUrl(sketchUrl);
        req.setPrompt(prompt);
        req.setRspImgType(rspImgType);

        // 发送请求
        return client.SketchToImage(req);
    }

    // 获取用户所有图像URL
    @Override
    public List<String> getAllImageUrls(Long userId) {
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");
        
        try {
            LambdaQueryWrapper<Image> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Image::getUserId, userId)
                    .eq(Image::getStatus, 0)
                    .orderByDesc(Image::getCreateTime);

            List<String> imageUrls = imageMapper.selectList(wrapper).stream()
                    .map(Image::getImageUrl)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            log.info("获取到用户 {} 的图像URL: {} 个", userId, imageUrls.size());
            return imageUrls;
        } catch (Exception e) {
            log.error("获取用户图像URL失败", e);
            throw new BusinessException("获取用户图像列表失败: " + e.getMessage());
        }
    }

    @Override
    public String styleConversion(StyleConversionDTO request, Long userId) throws Exception {
        ExceptionUtils.requireNonNull(request, "请求参数不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");
        ExceptionUtils.requireNonEmpty(request.getImageBase64(), "图片编码不能为空");
        
        try {
            IVisualService visualService = ModelUtils.createVisualService("cn-north-1");
            ImageStyleConversionRequest requestJson = new ImageStyleConversionRequest();
            requestJson.setImageBase64(request.getImageBase64());
            requestJson.setType(request.getType());
            //调用请求，拿取base64编码
            ImageStyleConversionResponse response = visualService.imageStyleConversion(requestJson);
            String image = response.getData().getImage();
            if (image == null || image.isEmpty()) {
                throw new BusinessException("获取转换后图片失败");
            }
            
            //存入数据库中，拿到Imageid
            String id = imageStorageService.saveBase64Image(image, userId);
            //通过imageId拿取url并返回
            return imageStorageService.getImageUrl(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("图片风格转换失败", e);
            throw new BusinessException("图片风格转换失败: " + e.getMessage());
        }
    }
}