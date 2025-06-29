package com.wtu.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wtu.DTO.user.LoginDTO;
import com.wtu.DTO.user.RegisterDTO;
import com.wtu.VO.LoginVO;
import com.wtu.entity.User;
import com.wtu.exception.AuthException;
import com.wtu.exception.BusinessException;
import com.wtu.exception.ExceptionUtils;
import com.wtu.mapper.UserMapper;
import com.wtu.properties.JwtProperties;
import com.wtu.service.AuthService;
import com.wtu.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    // IOC 注入
    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;

    @Override
    public String register(RegisterDTO dto) {
        ExceptionUtils.requireNonNull(dto, "注册信息不能为空");
        ExceptionUtils.requireNonEmpty(dto.getUsername(), "用户名不能为空");
        ExceptionUtils.requireNonEmpty(dto.getPassword(), "密码不能为空");
        ExceptionUtils.requireNonEmpty(dto.getEmail(), "邮箱不能为空");
        
        try {
            // 用户名重复判断
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUserName, dto.getUsername());
            if (userMapper.selectOne(wrapper) != null) {
                throw new AuthException("用户名已存在");
            }

            // 邮箱重复判断
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, dto.getEmail());
            if (userMapper.selectOne(emailWrapper) != null) {
                throw new AuthException("邮箱已注册");
            }

            // 保存用户
            User user = User.builder()
                    .userName(dto.getUsername())
                    .email(dto.getEmail())
                    .status(0)
                    .passWord(DigestUtil.md5Hex(dto.getPassword())) // 加密密码
                    .createTime(LocalDateTime.now())
                    .build();

            userMapper.insert(user);
            return "注册成功";
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("注册失败: " + e.getMessage());
        }
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        ExceptionUtils.requireNonNull(dto, "登录信息不能为空");
        ExceptionUtils.requireNonEmpty(dto.getUsername(), "用户名不能为空");
        ExceptionUtils.requireNonEmpty(dto.getPassword(), "密码不能为空");
        
        try {
            // 1. 查用户
            User user = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getUserName, dto.getUsername())
            );

            // 2. 判空 + 加密后密码比对
            String password = DigestUtil.md5Hex(dto.getPassword());
            if (user == null || !user.getPassWord().equals(password)) {
                throw new AuthException("用户名或密码错误");
            }

            // 3. 判断状态
            if (user.getStatus() != 0) {
                throw new AuthException("账号状态异常，请联系管理员");
            }

            // 4. 更新最后登录时间
            user.setLastLogin(LocalDateTime.now());
            userMapper.updateById(user);

            // 5. 生成 token
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getUserId());
            claims.put("userName", user.getUserName());

            String token = JwtUtil.createJwt(jwtProperties.getSecretKey(), jwtProperties.getTtl(), claims);

            // 6. 封装返回对象
            LoginVO loginVO = LoginVO.builder()
                    .userId(user.getUserId())
                    .userName(user.getUserName())
                    .token(token)
                    .build();

            return loginVO;
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("登录失败: " + e.getMessage());
        }
    }
}
