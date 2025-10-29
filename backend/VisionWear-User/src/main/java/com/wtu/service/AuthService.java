package com.wtu.service;

import com.wtu.dto.user.LoginDTO;
import com.wtu.dto.user.RegisterDTO;
import com.wtu.vo.LoginVO;
import jakarta.validation.Valid;

public interface AuthService {

    String register(@Valid RegisterDTO dto);

    LoginVO login(@Valid LoginDTO dto);
    
    /**
     * 用户退出登录
     * @param userId 用户ID
     */
    void logout(Long userId);
}
