package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tencentcloudapi.aiart.v20221229.AiartClient;
import com.tencentcloudapi.aiart.v20221229.models.SketchToImageRequest;
import com.tencentcloudapi.aiart.v20221229.models.SketchToImageResponse;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.wtu.DTO.ImageFusionDTO;
import com.wtu.DTO.ImageToImageDTO;
import com.wtu.DTO.SketchToImageDTO;
import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.ImageFusionVO;
import com.wtu.VO.ImageToImageVO;
import com.wtu.VO.SketchToImageVO;
import com.wtu.VO.TextToImageVO;
import com.wtu.config.StableDiffusionConfig;
import com.wtu.entity.Image;
import com.wtu.mapper.ImageMapper;
import com.wtu.service.ImageService;
import com.wtu.service.ImageStorageService;
import com.wtu.utils.ImageBase64Util;
import com.wtu.utils.TransApi;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import com.tencentcloudapi.common.Credential;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    // IOC 注入
    private final RestTemplate restTemplate;
    private final StableDiffusionConfig config;
    private final ObjectMapper objectMapper;
    private final ImageStorageService imageStorageService;
    private final ImageMapper imageMapper;
    private final ImageBase64Util imageBase64Util;

    // 百度翻译API
    private TransApi transApi;

    @Value("${vision.translate.appid}")
    private String appId;

    @Value("${vision.translate.security-key}")
    private String securityKey;

    // 初始化翻译API
    @PostConstruct
    public void init() {
        this.transApi = new TransApi(appId, securityKey);
    }

    // 提取通用的翻译方法
    private void translatePrompts(Object requestObj) {
        if (requestObj instanceof TextToImageDTO textRequest) {
            textRequest.setPrompt(translateToEnglish(textRequest.getPrompt()));
            if (textRequest.getNegativePrompt() != null && !textRequest.getNegativePrompt().isEmpty()) {
                textRequest.setNegativePrompt(translateToEnglish(textRequest.getNegativePrompt()));
            }
        } else if (requestObj instanceof ImageToImageDTO imageRequest) {
            imageRequest.setPrompt(translateToEnglish(imageRequest.getPrompt()));
            if (imageRequest.getNegativePrompt() != null && !imageRequest.getNegativePrompt().isEmpty()) {
                imageRequest.setNegativePrompt(translateToEnglish(imageRequest.getNegativePrompt()));
            }
        }
    }

    // 提取翻译方法
    private String translateToEnglish(String text) {
        try {
            String result = transApi.getTransResult(text, "zh", "en");
            JsonNode jsonNode = objectMapper.readTree(result);
            JsonNode transResult = jsonNode.path("trans_result");
            if (transResult.isArray() && !transResult.isEmpty()) {
                return transResult.get(0).path("dst").asText(text);
            }
            return text;
        } catch (Exception e) {
            log.error("翻译失败，使用原文本", e);
            return text;
        }
    }


    // 文本生成图像
    @Override
    public TextToImageVO textToImage(TextToImageDTO request, Long userId) throws Exception {
        // 翻译提示词
        translatePrompts(request);

        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        log.info("开始图像生成请求: {}, 提示: {}", requestId, request.getPrompt());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.set("Authorization", "Bearer " + config.getApiKey());

            ObjectNode requestBody = createTextToImageRequestBody(request);
            HttpEntity<String> httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            String modelEndpoint = "/generation/stable-diffusion-xl-1024-v1-0/text-to-image";
            String url = config.getBaseUrl() + modelEndpoint;

            String responseBody = restTemplate.postForObject(url, httpEntity, String.class);
            JsonNode responseJson = objectMapper.readTree(responseBody);
            List<TextToImageVO.GeneratedImage> generatedImages = parseResponse(responseJson, userId, TextToImageVO.GeneratedImage.class);

            long duration = System.currentTimeMillis() - startTime;
            log.info("图像生成完成: {}, 耗时: {}ms", requestId, duration);

            return TextToImageVO.builder()
                    .requestId(requestId)
                    .images(generatedImages)
                    .prompt(request.getPrompt())
                    .generationTimeMs(duration)
                    .build();

        } catch (HttpStatusCodeException e) {
            log.error("API请求失败: {}, 状态码: {}", requestId, e.getStatusCode());
            throw new Exception("调用 Stable Diffusion API 失败: " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            log.error("REST客户端异常: {}", requestId, e);
            throw new Exception("无法连接到Stable Diffusion API服务", e);
        } catch (Exception e) {
            log.error("图像生成过程中发生未预期的错误: {}", requestId, e);
            throw new Exception("图像生成失败: " + e.getMessage(), e);
        }
    }

    // 图像生成图像
    @Override
    public ImageToImageVO imageToImage(ImageToImageDTO request, Long userId) throws Exception {
        // 翻译提示词
        translatePrompts(request);

        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        log.info("开始以图生图请求: {}, 源图像Url: {}, 提示: {}", requestId, request.getSourceImageUrl(), request.getPrompt());

        try {
            // 获取源图像
            String sourceImageUrl = request.getSourceImageUrl();
            byte[] imageBytes = restTemplate.getForObject(sourceImageUrl, byte[].class);
            if (imageBytes == null) {
                throw new Exception("无法获取源图像数据");
            }

            // 准备请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Accept", "application/json");
            headers.set("Authorization", "Bearer " + config.getApiKey());

            // 准备请求体
            MultiValueMap<String, Object> body = createImageToImageMultipartBody(request, imageBytes);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 创建带有更长超时时间的RestTemplate
            RestTemplate timeoutRestTemplate = createTimeoutRestTemplate();

            String modelEndpoint = "/generation/stable-diffusion-xl-1024-v1-0/image-to-image";
            String url = config.getBaseUrl() + modelEndpoint;

            // 发送请求并获取响应
            String responseBody = timeoutRestTemplate.postForObject(url, requestEntity, String.class);
            JsonNode responseJson = objectMapper.readTree(responseBody);
            List<ImageToImageVO.GeneratedImage> generatedImages = parseResponse(responseJson, userId, ImageToImageVO.GeneratedImage.class);

            long duration = System.currentTimeMillis() - startTime;
            log.info("以图生图完成: {}, 耗时: {}ms", requestId, duration);

            return ImageToImageVO.builder()
                    .requestId(requestId)
                    .images(generatedImages)
                    .sourceImageUrl(request.getSourceImageUrl())
                    .prompt(request.getPrompt())
                    .generationTimeMs(duration)
                    .build();

        } catch (HttpStatusCodeException e) {
            log.error("API请求失败: {}, 状态码: {}", requestId, e.getStatusCode());
            throw new Exception("调用 Stable Diffusion API 失败: " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            log.error("REST客户端异常: {}", requestId, e);
            throw new Exception("无法连接到Stability AI API服务", e);
        } catch (Exception e) {
            log.error("以图生图过程中发生未预期的错误: {}", requestId, e);
            throw new Exception("以图生图失败: " + e.getMessage(), e);
        }
    }

    // 图片融合
    @Value("${vision.ttapi.api-key}")
    private String ttApiKey;

    @Override
    public ImageFusionVO imageFusion(ImageFusionDTO request, Long userId){
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        log.info("开始图片融合请求: {}, 图片url: {}", requestId, request.getImageUrlList());
        log.info("图片融合dto: {}", request);
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
            throw new RuntimeException("图片融合失败: " + (responseJson != null ? responseJson.path("message").asText() : "无响应"));
        }

        String jobId = responseJson.path("data").path("jobId").asText();
        log.info("图片融合任务提交成功，jobId={}", jobId);

        // 4. 只返回jobId，前端或调用方后续用jobId查询结果
        return ImageFusionVO.builder()
                .requestId(requestId)
                .jobId(jobId)  // 这里VO需新增jobId字段
                .generationTimeMs(System.currentTimeMillis() - startTime)
                .build();
    }


    // 线稿生图
    private static final String TENCENT_REGION = "ap-shanghai"; // 根据需求调整地域

    @Value("${vision.tencent.secret-id}")
    private String tencentSecretId;

    @Value("${vision.tencent.secret-key}")
    private String tencentSecretKey;

    @Override
    public SketchToImageVO sketchToImage(SketchToImageDTO request, Long userId) throws Exception {
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
            throw new Exception("线稿生图服务暂不可用: " + e.getMessage());
        } catch (Exception e) {
            log.error("线稿生图失败", e);
            throw new Exception("线稿生图失败: " + e.getMessage());
        }
    }


    // 图片融合查询图片生成结果
    public ImageFusionVO queryImageByJobId(String jobId, Long userId) {
        String apiKey = ttApiKey;
        String fetchUrl = "https://api.ttapi.io/midjourney/v1/fetch";
        log.info("查询图片融合结果，jobId: {}, userId: {}", jobId, userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("TT-API-KEY", apiKey);

        Map<String, String> body = new HashMap<>();
        body.put("jobId", jobId);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(fetchUrl, request, JsonNode.class);
        JsonNode responseJson = response.getBody();

        if (responseJson == null) {
            throw new RuntimeException("无响应");
        }

        String status = responseJson.path("status").asText();
        if (!"SUCCESS".equals(status)) {
            String msg = responseJson.path("message").asText();
            throw new RuntimeException("查询失败: " + msg);
        }

        JsonNode dataNode = responseJson.path("data");
        String cdnImage = dataNode.path("cdnImage").asText(null);
        if (cdnImage == null || cdnImage.isEmpty()) {
            throw new RuntimeException("未获取到图片地址");
        }

        // 假设 data 是你解析出来的 Data 对象
        String imageUrl = cdnImage;
        String imageId = imageStorageService.saveImageFromUrl(imageUrl, userId);
        String ossImageUrl = imageStorageService.getImageUrl(imageId);
        // 后续用 ossImageUrl 返回给前端或存数据库

        ImageFusionVO.GeneratedImage generatedImage = ImageFusionVO.GeneratedImage.builder()
                .imageId(imageId)
                .imageUrl(ossImageUrl)
                .build();

        return ImageFusionVO.builder()
                .requestId(UUID.randomUUID().toString())
                .images(Collections.singletonList(generatedImage))
                .generationTimeMs(0)
                .build();

    }


    // 辅助方法：生成文件名
    private String generateFileName(String url, Long userId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomStr = UUID.randomUUID().toString().substring(0, 8);
        String extension = url.contains(".png") ? ".png" :
                url.contains(".jpg") ? ".jpg" :
                        url.contains(".webp") ? ".webp" : ".png";

        return String.format("midjourney/%d/%s_%s%s", userId, timestamp, randomStr, extension);
    }


    private SketchToImageResponse callTencentSketchToImage(
            String sketchUrl,
            String prompt,
            String rspImgType
    ) throws TencentCloudSDKException {
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


    // 创建一个带有超时设置的RestTemplate
    private RestTemplate createTimeoutRestTemplate() {
        RestTemplate timeoutRestTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(60000);  // 60秒连接超时
        requestFactory.setReadTimeout(120000);    // 120秒读取超时
        timeoutRestTemplate.setRequestFactory(requestFactory);
        return timeoutRestTemplate;
    }

    // 创建图像到图像的请求体
    private MultiValueMap<String, Object> createImageToImageMultipartBody(ImageToImageDTO request, byte[] imageBytes) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // 添加初始图像
        ByteArrayResource imageResource = new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return "image.png";
            }
        };
        body.add("init_image", imageResource);

        // 添加提示词
        body.add("text_prompts[0][text]", request.getPrompt());
        body.add("text_prompts[0][weight]", "1.0");

        // 添加负面提示（如果有）
        if (request.getNegativePrompt() != null && !request.getNegativePrompt().isEmpty()) {
            body.add("text_prompts[1][text]", request.getNegativePrompt());
            body.add("text_prompts[1][weight]", "-1.0");
        }

        // 添加其他参数
        body.add("init_image_mode", "IMAGE_STRENGTH");
        body.add("image_strength", request.getImageStrength());
        body.add("cfg_scale", request.getCfgScale());
        body.add("samples", request.getSamples());
        body.add("steps", request.getSteps());
        body.add("seed", request.getSeed() != null ? request.getSeed() : 0);
        body.add("sampler", "K_DPMPP_2M");

        // 设置样式（如果有）
        if (request.getStyle() != null && !request.getStyle().isEmpty()) {
            body.add("style_preset", request.getStyle());
        }

        return body;
    }

    // 获取用户所有图像URL
    @Override
    public List<String> getAllImageUrls(Long userId) {
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
    }



    // 创建文本到图像的请求体
    private ObjectNode createTextToImageRequestBody(TextToImageDTO request) {
        ObjectNode requestBody = objectMapper.createObjectNode();

        // 添加文本提示
        ArrayNode textPrompts = objectMapper.createArrayNode();
        ObjectNode positivePrompt = objectMapper.createObjectNode();
        positivePrompt.put("text", request.getPrompt());
        positivePrompt.put("weight", 1.0);
        textPrompts.add(positivePrompt);

        // 添加负面提示（如果有）
        if (request.getNegativePrompt() != null && !request.getNegativePrompt().isEmpty()) {
            ObjectNode negativePrompt = objectMapper.createObjectNode();
            negativePrompt.put("text", request.getNegativePrompt());
            negativePrompt.put("weight", -1.0);
            textPrompts.add(negativePrompt);
        }

        requestBody.set("text_prompts", textPrompts);
        requestBody.put("cfg_scale", request.getCfgScale());
        requestBody.put("height", request.getHeight());
        requestBody.put("width", request.getWidth());
        requestBody.put("samples", request.getSamples());
        requestBody.put("steps", request.getSteps());
        requestBody.put("sampler", "K_DPMPP_2M");

        if (request.getStyle() != null && !request.getStyle().isEmpty()) {
            requestBody.put("style_preset", request.getStyle());
        }

        return requestBody;
    }

    // 通用响应解析方法，使用泛型来处理不同的VO类型
    private <T> List<T> parseResponse(JsonNode responseJson, Long userId, Class<T> clazz) {
        List<T> images = new ArrayList<>();
        JsonNode artifacts = responseJson.path("artifacts");

        if (artifacts.isArray()) {
            for (JsonNode artifact : artifacts) {
                if (!artifact.has("base64")) {
                    log.warn("响应中缺少base64字段");
                    continue;
                }

                String base64Image = artifact.path("base64").asText();
                String imageId = imageStorageService.saveBase64Image(base64Image, userId);
                String imageUrl = imageStorageService.getImageUrl(imageId);

                int width = artifact.has("width") ? artifact.path("width").asInt() : 1024;
                int height = artifact.has("height") ? artifact.path("height").asInt() : 1024;
                long seed = artifact.has("seed") ? artifact.path("seed").asLong() : 0;

                // 根据类型创建不同的VO对象
                try {
                    Object imageObj;
                    if (clazz == TextToImageVO.GeneratedImage.class) {
                        imageObj = TextToImageVO.GeneratedImage.builder()
                                .imageId(imageId)
                                .imageUrl(imageUrl)
                                .width(width)
                                .height(height)
                                .seed(seed)
                                .build();
                    } else {
                        imageObj = ImageToImageVO.GeneratedImage.builder()
                                .imageId(imageId)
                                .imageUrl(imageUrl)
                                .width(width)
                                .height(height)
                                .seed(seed)
                                .build();
                    }
                    images.add((T) imageObj);
                } catch (ClassCastException e) {
                    log.error("类型转换失败", e);
                }
            }
        } else {
            log.warn("API响应中未找到artifacts数组或格式不正确");
        }

        return images;
    }
}