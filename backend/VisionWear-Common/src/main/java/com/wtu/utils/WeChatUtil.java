package com.wtu.utils;

import com.alibaba.fastjson.JSON;
import com.wtu.entity.TokenInfo;
import com.wtu.entity.WeChatUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/05/11/14:34
 * @Description: 解包微信登录信息工具类
 */
@Slf4j
@Component
public class WeChatUtil {

   private static final String appId=null;
   private static final String secret=null;
    @Autowired
    private static RestTemplate restTemplate;
    /**
     * @description: 获取微信用户信息
     * @param: code 授权码
     * @return: 用户信息
     * @author gaochen
     * @date: 2025/5/11 14:41
     */
    public static WeChatUser getUserInfo(String code){
        String tokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" +appId  + "&secret=" + secret + "&code=" +code+ "&grant_type=authorization_code";
        String  tokenResponse=restTemplate.getForObject(tokenUrl, String.class);
        log.info("获取到accessToken返回结果:{}",tokenResponse);
        //将结果封装到TokenInfo对象中
        TokenInfo tokenInfo = JSON.parseObject(tokenResponse, TokenInfo.class);
        //用accessToken获取扫码人的信息
        String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + tokenInfo.getAccessToken()
                +"&openid="+tokenInfo.getOpenid()+"&lang=zn_CN";
        // 使用RestTemplate获取用户信息
        String userInfoResponse = restTemplate.getForObject(userInfoUrl, String.class);
        log.info("获取个人用户信息返回:{}", userInfoResponse);

        // 将获取的信息转化为WeChatUser对象
        return JSON.parseObject(userInfoResponse, WeChatUser.class);

    }


}
