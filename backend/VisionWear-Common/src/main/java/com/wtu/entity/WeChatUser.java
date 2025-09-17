package com.wtu.entity;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/05/11/14:35
 * @Description:
 */
@Data
public class WeChatUser {
    //用户唯一标识
    private String openid;
    //用户昵称
    private String nickname;
    //用户性别， 1——男 2——女 0——未知
    private Integer sex;
    //用户国家
    private String country;
    //用户个人资料填写的城市
    private String city;
    //用户所在省份
    private String province;
    //用户头像
    private String headimgurl;
    //用户特权信息
    private String privilege;
    //只有用户将信息绑定到微信开放平台账号后，才会生成该字段
    private String unionid;
}
