package com.wtu.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencentcloudapi.aiart.v20221229.AiartClient;
import com.tencentcloudapi.aiart.v20221229.models.SketchToImageRequest;
import com.tencentcloudapi.aiart.v20221229.models.SketchToImageResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;
import com.wtu.DTO.ImageToImageDTO;
import com.wtu.DTO.SketchToImageDTO;
import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.ImageToImageVO;
import com.wtu.VO.SketchToImageVO;
import com.wtu.VO.TextToImageVO;
import com.wtu.config.StableDiffusionConfig;
import com.wtu.entity.Image;
import com.wtu.mapper.ImageMapper;
import com.wtu.service.ImageService;
import com.wtu.service.ImageStorageService;
import com.wtu.utils.ModelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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

    @Value("${vision.doubao.ak}")
    private  String accessKey;
    @Value("${vision.doubao.sk}")
    private  String secretKey;
    // 文本生成图像
    @Override
    public List<String> textToImage(TextToImageDTO request, Long userId) throws Exception {
        List<String> ids = new ArrayList<>();

        IVisualService visualService = VisualServiceImpl.getInstance("cn-north-1");
        //输入AK和SK进行鉴权
        visualService.setAccessKey(accessKey);
        visualService.setSecretKey(secretKey);
        //用工具类，转为JsonObject类型
        JSONObject req = ModelUtils.toJsonObject(request);
        //如果用户prompt字数过少，则开启自动文本优化
        if (request.getPrompt().length() < 10) {
            req.put("use_pre_llm", "true");
        }
        log.info("req:{}", req.toString());
        //发送请求，得到response
        Object response = visualService.cvProcess(req);
        //从response中拿出base64编码
        JSONArray base64Array = ModelUtils.getBase64(response);
        for (int i = 0; i < base64Array.size(); i++) {

            String valid = base64Array.getString(i);
            //如果有前缀"," ，则删掉
            //TODO 这个删前缀的方式可能有bug，也许应该把+1去掉，有待观察
            if (valid.contains(",")) {
                valid = valid.substring(valid.indexOf(",") + 1);
            }
            //对base64进行解码并上传,得到ImageID,存入ids中
            ids.add(imageStorageService.saveBase64Image(valid, userId));
        }

        return ids;
    }

    // 图像生成图像
    @Override
    public ImageToImageVO imageToImage(ImageToImageDTO request, Long userId) throws Exception {
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