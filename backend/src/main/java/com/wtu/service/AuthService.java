package com.wtu.service;

import com.wtu.DTO.user.LoginDTO;
import com.wtu.DTO.user.RegisterDTO;
import com.wtu.VO.LoginVO;
import jakarta.validation.Valid;

public interface AuthService {

    String register(@Valid RegisterDTO dto);

    LoginVO login(@Valid LoginDTO dto);
}
