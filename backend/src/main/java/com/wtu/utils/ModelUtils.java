package com.wtu.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wtu.exception.ServiceException;

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
     * @return base64 JsonArray类型
     */
    public static JSONArray getBase64(Object obj) {
        JSONObject jsonObject = toJsonObject(obj);

        if (!jsonObject.containsKey("data")) {
            throw new ServiceException("源对象data数据为空，无法拿取base64");
        } else {
            Object data = jsonObject.get("data");
            if (data instanceof JSONObject) {

                JSONObject JsonData = (JSONObject) data;
                if (JsonData.containsKey("binary_data_base64")) {
                    return (JSONArray) JsonData.get("binary_data_base64");
                } else {
                    throw new ServiceException("未找到base64编码");
                }
            } else {
                throw new ServiceException("只支持JSONObject类型的data数据");
            }
        }

    }
}
