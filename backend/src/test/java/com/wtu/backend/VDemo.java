package com.wtu.backend;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;

/**
 * @author gaochen
 * @description: 文生图demo
 * @date 2025/6/23 10:53
 */
public class VDemo {


    public static void main(String[] args) throws Exception {


        IVisualService visualService = VisualServiceImpl.getInstance("cn-north-1");
        //输入AK和SK进行鉴权
        visualService.setAccessKey("AKLTNWVjMmZkNmFlYzVmNGFkYjkzZTkyNWYyN2EwMDIzNjU");
        visualService.setSecretKey("WkRNNE5EZGlOR1kyT1dNek5HRXhZMkUyTkRsalpUTmtZVFl5WkRFd05qZw==");

        JSONObject req = new JSONObject();
        //请求Body(查看接口文档请求参数-请求示例，将请求参数内容复制到此)
        req.put("req_key", "high_aes_general_v30l_zt2i");
        req.put("prompt", "一只狗");
        req.put("return_url", "true");

        JSONObject logoInfo= new JSONObject();
        logoInfo.put("add_logo","True");
        logoInfo.put("position","0");
        logoInfo.put("logo_text_content","WTU-vision");
        req.put("logo_info",logoInfo);


        try {
            Object response = visualService.cvProcess(req);
            //对象转Json字段，类型是String
            String jsonString = JSON.toJSONString(response);
            //想拿去json中指定字段，需要先封装成json类型
            JSONObject jsonObject = JSON.parseObject(jsonString);
            //API中，图片URL在data中，所以需要拿取jsonObject中的data值，再将data值封装为json类型，然后从data的json类型中拿取URL
            JSONObject data = (JSONObject)jsonObject.get("data");
            Boolean isSuccess =data.containsKey("binary_data_base64");
            System.out.println("是否有值:"+isSuccess);
            //如果成功，则进行解码
            if(isSuccess){
                //数组类型是JsonArray(alibaba.fastJson)类型
                JSONArray DataBase64 = (JSONArray)data.get("binary_data_base64");
                for(int i=0;i<DataBase64.size();i++){

                    String valid = DataBase64.getString(i);
                    //如果有前缀”，“，则删掉
                    if(valid.contains(",")){
                       valid = valid.substring(valid.indexOf(",")+1);
                    }
                    //对base64进行解码
                    byte[] decode = Base64.getDecoder().decode(valid);
                    //用IO流将byte数组写出到文件中，即可得到图片
                    OutputStream stream = new BufferedOutputStream(new FileOutputStream("text"+i+".png"));
                    stream.write(decode);
                    System.out.println("图片保存成功: text" + i + ".png");
                }
            }




        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
