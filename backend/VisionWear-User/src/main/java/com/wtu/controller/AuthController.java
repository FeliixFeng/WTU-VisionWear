package com.wtu.controller;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alibaba.fastjson.JSON;
import com.wtu.dto.user.LoginDTO;
import com.wtu.dto.user.RegisterDTO;
import com.wtu.dto.user.WxSignatureDTO;
import com.wtu.vo.LoginVO;
import com.wtu.entity.WeChatUser;
import com.wtu.exception.AuthException;
import com.wtu.result.Result;
import com.wtu.service.AuthService;
import com.wtu.utils.UserContext;
import com.wtu.utils.WeChatUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证模块")
@Slf4j
public class AuthController {
    @Autowired
    private  AuthService authService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO dto) {
        String msg = authService.register(dto);
        if ("注册成功".equals(msg)) {
            return Result.success(null, msg);
        } else {
            return Result.error(msg);
        }
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        LoginVO loginVO = authService.login(dto);
        return Result.success(loginVO);
    }
    
    @Operation(summary = "用户退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // ✅ 从 ThreadLocal 获取 userId（UserInfoInterceptor 已设置）
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new AuthException("未登录");
        }
        
        authService.logout(userId);
        return Result.success(null, "退出登录成功");
    }

    @Operation(summary = "微信登录校验")
    @GetMapping("/wxCheck")
    public String wxCheck(@ModelAttribute WxSignatureDTO wxSignatureDTO){
            //进行校验是是否是微信登录请求
            log.info("收到微信登录校验:{}",wxSignatureDTO);
            StringBuilder stringBuilder=new StringBuilder();

        ArrayList<String> array=new ArrayList<>();
        array.add("WTU");//token名为WTU
        array.add(wxSignatureDTO.getTimestamp());
        array.add(wxSignatureDTO.getNonce());
        //将token nonce timestamp按照字典项排序,然后拼接
        Collections.sort(array);
        for(String item : array){
            stringBuilder.append(item);
        }
        //进行一个sha1加密
        String auth =DigestUtils.sha1Hex(stringBuilder.toString());
        log.info("auth:{}",auth);
        if(auth.equals(wxSignatureDTO.getSignature())){
            //校验成功，原样返回
            return wxSignatureDTO.getEchostr();
        }else{
            log.info("signature:{}",wxSignatureDTO.getSignature());
            throw new AuthException("微信登录校验失败！");
        }

    }

    @Operation(summary = "用户微信登录")
    @GetMapping("/wxLogin")
    public void wxLoginPage(HttpServletResponse response) throws IOException {
        //设置回调URL
        String redirectURL= URLEncoder.encode("http://fe6bd74.r9.cpolar.top/wxCallback", StandardCharsets.UTF_8);

        String URL="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxf82120c7638e1d27&redirect_uri="+redirectURL+
                "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

        response.setContentType("image/png");
        QrCodeUtil.generate(URL,300,300,"jpg",response.getOutputStream());
    }

    @RequestMapping("/wxCallback")
    public String pcCallback(String code, String state, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
        WeChatUser userInfo = WeChatUtil.getUserInfo(code);
        //TODO 缓存用户信息，构造session
        return JSON.toJSONString(userInfo);
    }


}
