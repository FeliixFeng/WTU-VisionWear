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
import com.wtu.DTO.ImageFusionDTO;
import com.wtu.DTO.ImageToImageDTO;
import com.wtu.DTO.SketchToImageDTO;
import com.wtu.DTO.TextToImageDTO;
import com.wtu.VO.ImageFusionVO;
import com.wtu.VO.SketchToImageVO;
import com.wtu.entity.Image;
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
    private ImageBase64Util imageBase64Util;

    // 文本生成图像
    @Override
    public List<String> textToImage(TextToImageDTO request, Long userId) throws Exception {
        List<String> ids = new ArrayList<>();
        //用工具类快速构造VisualService
        IVisualService visualService=ModelUtils.createVisualService("cn-north-1");
        //用工具类转为JsonObject类型
        JSONObject req = ModelUtils.toJsonObject(request);
        //如果用户prompt字数过少，则开启自动文本优化
        if (request.getPrompt().length() < 10) {
            req.put("use_pre_llm", true);
        }
        log.info("req:{}", req.toString());
        //发送请求，得到response
        Object response = visualService.cvProcess(req);
        //从response中拿出base64编码
        List<String> base64Array = ModelUtils.getBase64(response);
        for (String s : base64Array) {
            ids.add(imageStorageService.saveBase64Image(s, userId));
        }

        return ids;
    }

    // 图像生成图像
    @Override
    public List<String> imageToImage(ImageToImageDTO request, Long userId) throws Exception {

        List<String> ids = new ArrayList<>();
        JSONObject jsonRequest = ModelUtils.toJsonObject(request);
        log.info("jsonRequest:{}", jsonRequest.toString());
        //用工具类构造IvisualService实例
        IVisualService visualService = ModelUtils.createVisualService("cn-north-1");
        //先得到taskID
        String taskId = (String) visualService.cvSync2AsyncSubmitTask(jsonRequest);
        //用taskID去申请另一个接口，得到图片
        JSONObject taskRequest = new JSONObject();
        taskRequest.put("req_key",request.getReqKey());
        taskRequest.put("task_id",taskId);

        log.info("taskRequest:{}", taskRequest);

        JSONObject taskResponse = (JSONObject)visualService.cvSync2AsyncGetResult(taskRequest);
        List<String> base64 = ModelUtils.getBase64(taskResponse);
        for (String s : base64) {
            //对base64进行解码并上传,得到ImageID,存入ids中
            ids.add(imageStorageService.saveBase64Image(s, userId));
        }

        return ids;
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
        // 这里VO需新增jobId字段
        return ImageFusionVO.builder()
                .requestId(requestId)
                .jobId(jobId)
                .generationTimeMs(System.currentTimeMillis() - startTime)
                .build();
    }

    @Override
    public ImageFusionVO queryImageByJobId(String jobId, Long userId){
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

}