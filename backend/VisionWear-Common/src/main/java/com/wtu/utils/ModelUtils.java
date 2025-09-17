package com.wtu.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;
import com.wtu.exception.ServiceException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author gaochen
 * @description: 便捷性工具类，目前可帮助快速构造Json
 * @date 2025/6/23 15:37
 */
public class ModelUtils {

    private static final SerializeConfig serializeConfig = new SerializeConfig();

    static {
        //将命名策略改为驼峰转下划线
        serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }

    /**
     * 将任意对象转换为JSONObject
     *
     * @param obj 任意对象
     * @return JSONObject对象
     */
    public static JSONObject toJsonObject(Object obj) {
        if (obj == null) {
            return new JSONObject();
        }

        if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        }

        if (obj instanceof String) {
            return JSONObject.parseObject(obj.toString());
        }

        return JSONObject.parseObject(JSONObject.toJSONString(obj, serializeConfig,
                SerializerFeature.DisableCircularReferenceDetect));

    }

    /**
     * 拿取JsonObject中的base64编码
     * 只拿取base64，不进行解码操作
     *
     * @param obj 任意对象,目前只支持JsonObject进行有效拿取
     * @return base64 集合类型
     */
    public static List<String> getBase64(Object obj) {
        JSONObject jsonObject = toJsonObject(obj);
        List<String> base64Array = new ArrayList<String>();
        if (!jsonObject.containsKey("data")) {
            throw new ServiceException("源对象data数据为空，无法拿取base64");
        } else {
            Object data = jsonObject.get("data");
            if (data instanceof JSONObject) {

                JSONObject JsonData = (JSONObject) data;
                if (JsonData.containsKey("binary_data_base64")) {
                    JSONArray array = (JSONArray) JsonData.get("binary_data_base64");
                    for (int i = 0; i < array.size(); i++) {
                        String valid = array.getString(i);
                        //如果有前缀"," ，则删掉
                        if (valid.contains(",")) {
                            valid = valid.substring(valid.indexOf(",") + 1);
                        }
                        base64Array.add(valid);
                    }
                    return base64Array;
                } else {
                    throw new ServiceException("未找到base64编码");
                }
            } else {
                throw new ServiceException("只支持JSONObject类型的data数据");
            }
        }

    }
    /**
     * 帮我们创建VisualService实例，不需要再一个个的赋值ak sk了
     *
     * @param region 具体数值看火山引擎对应模型给的action
     * @return VisualService实例
     */
    public static IVisualService createVisualService(String region) throws Exception {
        YamlPropertiesFactoryBean yamlProperties = new YamlPropertiesFactoryBean();
        yamlProperties.setResources(new ClassPathResource("application-dev.yml"));
        Properties properties = yamlProperties.getObject();
        String ak = (String) properties.get("vision.doubao.ak");
        String sk = (String) properties.get("vision.doubao.sk");
        IVisualService visualService = VisualServiceImpl.getInstance(region);
        visualService.setAccessKey(ak);
        visualService.setSecretKey(sk);
        return visualService;
    }

    public static IVisualService createVisualService() throws Exception {
        YamlPropertiesFactoryBean yamlProperties = new YamlPropertiesFactoryBean();
        yamlProperties.setResources(new ClassPathResource("application-dev.yml"));
        Properties properties = yamlProperties.getObject();
        String ak = (String) properties.get("vision.doubao.ak");
        String sk = (String) properties.get("vision.doubao.sk");
        IVisualService visualService = VisualServiceImpl.getInstance();
        visualService.setAccessKey(ak);
        visualService.setSecretKey(sk);
        return visualService;
    }

    /**
     * 快速的从Json格式中拿取data数据中的某个字段，
     *跳过先前手动 拿取data 拿取字段 的逻辑
     * @param request 整个Json串
     * @param key 想在data中拿取的字段名称
     * @param clazz 字段类型
     * @return 目标字段的值
     */
    public static <T> T getFromJsonData(JSONObject request, String key, Class<T> clazz) {
        if (request.containsKey("data")) {
            JSONObject data = request.getJSONObject("data");
            if (data.containsKey(key)) {
                Object value = data.get(key);
                if (clazz.isInstance(value)) {
                    return clazz.cast(value);
                } else {
                    // 处理JSONObject转String的特殊情况
                    if (clazz == String.class && value instanceof JSONObject) {
                        return (T) value.toString();
                    }
                    // 处理其他类型转换
                    try {
                        return JSON.parseObject(value.toString(), clazz);
                    } catch (Exception e) {
                        throw new ServiceException("无法将" + value.getClass().getSimpleName() + "转换为" + clazz.getSimpleName() + 
                            "，值为：" + value);
                    }
                }
            } else {
                throw  new ServiceException("data数据体中不存在" + key + "字段");
            }
        } else {
            throw new ServiceException("数据不存在data字段!");
        }
    }
}
