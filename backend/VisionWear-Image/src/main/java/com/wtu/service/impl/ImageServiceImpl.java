package com.wtu.service.impl;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
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
import com.wtu.dto.image.*;
import com.wtu.vo.DoodleToImageByTYVO;
import com.wtu.vo.ImageFusionVO;
import com.wtu.vo.SketchToImageByTYVO;
import com.wtu.vo.SketchToImageVO;
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

    // 线稿生图
    // 根据需求调整地域
    private static final String TENCENT_REGION = "ap-shanghai";

    private final RestTemplate restTemplate;
    private final ImageStorageService imageStorageService;
    private final ImageMapper imageMapper;
    private final ImageBase64Util imageBase64Util;
    // 通义-文生图功能
    @Value("${vision.aliyun.api-key}")
    private String aliyunApiKey;
    @Value("${vision.tencent.secret-id}")
    private String tencentSecretId;
    @Value("${vision.tencent.secret-key}")
    private String tencentSecretKey;
    @Value("${vision.ttapi.api-key}")
    private String ttApiKey;

    // 文本生成图像
    @Override
    public List<String> textToImage(TextToImageDTO request, Long userId) {
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

            if (base64Array.isEmpty()) {
                throw new BusinessException("未能生成有效的图像");
            }

            for (String s : base64Array) {
                ids.add(imageStorageService.saveBase64Image(s, userId));
            }

            return ids;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("文本生成图像失败: " + e.getMessage());
        }
    }

    @Override
    public List<String> textToImageByTongyi(TextToImageByTYDTO request, Long userId) {
        ExceptionUtils.requireNonNull(request, "请求参数不能为空");
        ExceptionUtils.requireNonNull(request.getPrompt(), "生成提示词不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");

        try {
            ImageSynthesisParam.ImageSynthesisParamBuilder builder = ImageSynthesisParam.builder()
                    .apiKey(aliyunApiKey)
                    .model(request.getModel())
                    .prompt(request.getPrompt())
                    .n(request.getN())
                    .size(request.getSize());

            if (request.getNegative_prompt() != null && !request.getNegative_prompt().isEmpty()) {
                builder.negativePrompt(request.getNegative_prompt());
            }
            if (request.getSeed() != null && request.getSeed() > 0) {
                builder.seed(request.getSeed());
            }
            ImageSynthesisParam param = builder.build();

            ImageSynthesis imageSynthesis = new ImageSynthesis();
            ImageSynthesisResult result = imageSynthesis.call(param); // 同步调用

            if (result == null || result.getOutput() == null || result.getOutput().getResults() == null) {
                throw new BusinessException("未能获取到图像生成结果");
            }

            List<String> ids = new ArrayList<>();
            for (var img : result.getOutput().getResults()) {
                String imageUrl = img.get("url");
                if (imageUrl == null || imageUrl.isEmpty()) continue;
                String imageId = imageStorageService.saveImageFromUrl(imageUrl, userId);
                ids.add(imageId);
            }

            if (ids.isEmpty()) {
                throw new BusinessException("未能生成有效的图像");
            }
            return ids;

        } catch (ApiException | NoApiKeyException e) {
            log.error("通义万象SDK调用失败", e);
            throw new BusinessException("通义万象SDK调用失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文本生成图像失败", e);
            throw new BusinessException("文本生成图像失败: " + e.getMessage());
        }
    }

    // 通义-线稿成图功能（使用wanx2.1-imageedit模型）
    @Override
    public SketchToImageByTYVO sketchToImageByTongyi(SketchToImageByTYDTO request, Long userId) {
        ExceptionUtils.requireNonNull(request, "请求参数不能为空");
        ExceptionUtils.requireNonNull(request.getPrompt(), "生成提示词不能为空");
        ExceptionUtils.requireNonNull(request.getBaseImageUrl(), "基础图像URL不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");

        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();

        String baseImageUrl = request.getBaseImageUrl();

        try {
            // 构建parameters参数
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("is_sketch", request.getIsSketch());
            if (request.getPromptExtend() != null) {
                parameters.put("prompt_extend", request.getPromptExtend());
            }

            // 构建SDK调用参数
            ImageSynthesisParam.ImageSynthesisParamBuilder builder = ImageSynthesisParam.builder()
                    .apiKey(aliyunApiKey)
                    .model(request.getModel())
                    .function(ImageSynthesis.ImageEditFunction.DOODLE)
                    .prompt(request.getPrompt())
                    .baseImageUrl(baseImageUrl)
                    .n(request.getN())
                    .size(request.getSize())
                    .parameters(parameters);

            // 如果有种子参数，添加到builder
            if (request.getSeed() != null && request.getSeed() > 0) {
                builder.seed(request.getSeed());
            }

            ImageSynthesisParam param = builder.build();

            // 调用SDK
            ImageSynthesis imageSynthesis = new ImageSynthesis();
            ImageSynthesisResult result = imageSynthesis.call(param);

            if (result == null || result.getOutput() == null || result.getOutput().getResults() == null) {
                throw new BusinessException("未能获取到图像生成结果");
            }

            List<SketchToImageByTYVO.GeneratedImage> imageList = new ArrayList<>();
            for (var img : result.getOutput().getResults()) {
                String imageUrl = img.get("url");
                int width = img.containsKey("width") ? Integer.parseInt(img.get("width")) : 0;
                int height = img.containsKey("height") ? Integer.parseInt(img.get("height")) : 0;
                long seed = img.containsKey("seed") ? Long.parseLong(img.get("seed")) : 0L;

                String imageId = imageStorageService.saveImageFromUrl(imageUrl, userId);

                imageList.add(SketchToImageByTYVO.GeneratedImage.builder()
                        .imageId(imageId)
                        .imageUrl(imageUrl)
                        .width(width)
                        .height(height)
                        .seed(seed)
                        .build());
            }

            return SketchToImageByTYVO.builder()
                    .requestId(requestId)
                    .images(imageList)
                    .prompt(request.getPrompt())
                    .baseImageUrl(baseImageUrl)
                    .generationTimeMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (ApiException | NoApiKeyException e) {
            log.error("通义万象SDK调用失败", e);
            throw new BusinessException("通义万象SDK调用失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("线稿成图失败", e);
            throw new BusinessException("线稿成图失败: " + e.getMessage());
        }
    }

    // 通义-涂鸦生图功能
    @Override
    public DoodleToImageByTYVO DoodleToImageByTongyi(DoodleToImageByTYDTO request, Long userId) {
        ExceptionUtils.requireNonNull(request, "请求参数不能为空");
        ExceptionUtils.requireNonNull(request.getPrompt(), "生成提示词不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");

        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();

        String sketchImageUrl = request.getSketchImageUrl();

        if (sketchImageUrl == null || sketchImageUrl.isEmpty()) {
            throw new BusinessException("涂鸦图像不能为空");
        }

        try {
            // 构建参数，完全对齐官方SDK
            ImageSynthesisParam param = ImageSynthesisParam.builder()
                    .apiKey(aliyunApiKey)
                    .model(request.getModel())
                    .prompt(request.getPrompt())
                    .n(request.getN())
                    .size(request.getSize())
                    .sketchImageUrl(sketchImageUrl)
                    .style(request.getStyle())
                    .seed(request.getSeed())
                    .build();

            // 调用SDK
            ImageSynthesis imageSynthesis = new ImageSynthesis("image2image");
            ImageSynthesisResult result = imageSynthesis.call(param);

            if (result == null || result.getOutput() == null || result.getOutput().getResults() == null) {
                throw new BusinessException("未能获取到图像生成结果");
            }

            List<DoodleToImageByTYVO.GeneratedImage> imageList = new ArrayList<>();
            for (var img : result.getOutput().getResults()) {
                String imageUrl = img.get("url");
                int width = img.containsKey("width") ? Integer.parseInt(img.get("width")) : 0;
                int height = img.containsKey("height") ? Integer.parseInt(img.get("height")) : 0;
                long seed = img.containsKey("seed") ? Long.parseLong(img.get("seed")) : 0L;
                String style = img.get("style");
                String imageId = imageStorageService.saveImageFromUrl(imageUrl, userId);

                imageList.add(DoodleToImageByTYVO.GeneratedImage.builder()
                        .imageId(imageId)
                        .imageUrl(imageUrl)
                        .width(width)
                        .height(height)
                        .seed(seed)
                        .style(style)
                        .build());
            }

            return DoodleToImageByTYVO.builder()
                    .requestId(requestId)
                    .images(imageList)
                    .prompt(request.getPrompt())
                    .sketchImageUrl(sketchImageUrl)
                    .generationTimeMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (ApiException | NoApiKeyException e) {
            log.error("通义万象SDK调用失败", e);
            throw new BusinessException("通义万象SDK调用失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("涂鸦作图失败", e);
            throw new BusinessException("涂鸦作图失败: " + e.getMessage());
        }
    }

    // 图像生成图像
    @Override
    public List<String> imageToImage(ImageToImageDTO request, Long userId) {
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
                    if (base64.isEmpty()) {
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
            throw new BusinessException("图像生成失败: " + e.getMessage());
        }
    }

    @Override
    public SketchToImageVO sketchToImage(SketchToImageDTO request, Long userId) {
        ExceptionUtils.requireNonNull(request, "请求参数不能为空");
        ExceptionUtils.requireNonNull(request.getSketchImageURL(), "线稿图URL不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");

        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();

        try {
            // 1. 获取线稿图URL
            String sketchUrl = request.getSketchImageURL();
            if (!sketchUrl.startsWith("http")) {
                sketchUrl = imageStorageService.getImageUrl(sketchUrl);
            }

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

            String imageId = imageStorageService.saveBase64Image(resultImageUrl, userId);

            return SketchToImageVO.builder()
                    .requestId(requestId)
                    .images(List.of(
                            SketchToImageVO.GeneratedImage.builder()
                                    .imageId(imageId)
                                    .imageUrl(imageStorageService.getImageUrl(imageId))
                                    .build()
                    ))
                    .generationTimeMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (TencentCloudSDKException e) {
            throw new BusinessException("线稿生图服务暂不可用: " + e.getMessage());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("线稿生图失败: " + e.getMessage());
        }
    }

    @Override
    public ImageFusionVO imageFusion(ImageFusionDTO request, Long userId) {
        ExceptionUtils.requireNonNull(request, "请求参数不能为空");
        ExceptionUtils.requireNonNull(request.getImageUrlList(), "图片URL列表不能为空");
        ExceptionUtils.requireTrue(!request.getImageUrlList().isEmpty(), "图片URL列表不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");

        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();

        try {

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


            // 4. 只返回jobId，前端或调用方后续用jobId查询结果
            return ImageFusionVO.builder()
                    .requestId(requestId)
                    .jobId(jobId)
                    .generationTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("图片融合失败: " + e.getMessage());
        }
    }

    @Override
    public ImageFusionVO queryImageByJobId(String jobId, Long userId) {
        ExceptionUtils.requireNonEmpty(jobId, "任务ID不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");

        try {
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

            // 检查任务状态
            if ("FAILED".equals(status)) {
                String msg = responseJson.path("message").asText();
                throw new BusinessException("任务失败: " + msg);
            }

            JsonNode dataNode = responseJson.path("data");
            String progress = dataNode.path("progress").asText("0");
            
            // 如果任务未完成，返回进度信息
            if (Integer.parseInt(progress) < 100) {
                log.info("任务 {} 当前进度: {}%", jobId, progress);
                return ImageFusionVO.builder()
                        .requestId(UUID.randomUUID().toString())
                        .jobId(jobId)
                        .progress(Integer.parseInt(progress))
                        .generationTimeMs(0)
                        .build();
            }

            // 任务完成，获取图片
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
                    .jobId(jobId)
                    .images(Collections.singletonList(generatedImage))
                    .progress(100)
                    .generationTimeMs(0)
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("查询图片融合结果失败: " + e.getMessage());
        }
    }

    @Override
    public String getFusionProgress(String jobId, Long userId) {
        ExceptionUtils.requireNonEmpty(jobId, "任务ID不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");

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

        return responseJson.path("data").get("progress").asText();
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

            return imageMapper.selectList(wrapper).stream()
                    .map(Image::getImageUrl)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new BusinessException("获取用户图像列表失败: " + e.getMessage());
        }
    }

    @Override
    public String styleConversion(StyleConversionDTO request, Long userId) {
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
            throw new BusinessException("图片风格转换失败: " + e.getMessage());
        }
    }


}