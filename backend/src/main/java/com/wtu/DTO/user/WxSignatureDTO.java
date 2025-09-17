package com.wtu.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/05/11/10:03
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WxSignatureDTO {
    //签名，包含token 和 nonce timestamp参数
    private String signature;
    //时间戳
    private String timestamp;
    //随机数
    private String nonce;
    //随机字符串
    private String echostr;
}
