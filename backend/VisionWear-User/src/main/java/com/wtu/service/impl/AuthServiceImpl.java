package com.wtu.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wtu.dto.user.LoginDTO;
import com.wtu.dto.user.RegisterDTO;
import com.wtu.properties.JwtProperties;
import com.wtu.vo.LoginVO;
import com.wtu.entity.User;
import com.wtu.exception.AuthException;
import com.wtu.exception.BusinessException;
import com.wtu.exception.ExceptionUtils;
import com.wtu.mapper.UserMapper;
import com.wtu.service.AuthService;
import com.wtu.utils.JwtUtil;
import com.wtu.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    // IOC 注入
    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;
    private final RedisUtil redisUtil;

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
            //设置默认的昵称
            String prefix="DeepVision@";
            StringBuilder stringBuilder=new StringBuilder();
            int remainLength=20-prefix.length();
            String baseChar="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            for (int i = 0;i<remainLength;i++){
                Random random=new Random();
                int randomInt = random.nextInt(baseChar.length());
                stringBuilder.append(baseChar.charAt(randomInt));
            }
            String nickName=prefix + stringBuilder;

            // 保存用户
            User user = User.builder()
                    .userName(dto.getUsername())
                    .email(dto.getEmail())
                    .status(0)
                    .passWord(DigestUtil.md5Hex(dto.getPassword())) // 加密密码
                    .createTime(LocalDateTime.now())
                    .nickName(nickName)
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
            log.info("dto:{}",dto);
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

            // 5. 生成 Access Token（短期，15分钟）
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getUserId());
            claims.put("userName", user.getUserName());
            String accessToken = JwtUtil.createJwt(
                    jwtProperties.getSecretKey(), 
                    jwtProperties.getTtl(), 
                    claims
            );

            // 6. 生成 Refresh Token（UUID）
            String refreshToken = UUID.randomUUID().toString().replace("-", "");

            // 7. 将 Token 信息存入 Redis
            saveTokenToRedis(user.getUserId(), user.getUserName(), accessToken, refreshToken);

            // 8. 封装返回对象
            LoginVO loginVO = LoginVO.builder()
                    .userId(user.getUserId())
                    .userName(user.getUserName())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            log.info("用户 {} 登录成功", user.getUserName());
            return loginVO;
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("登录失败: " + e.getMessage());
        }
    }

    /**
     * 将 Token 信息保存到 Redis
     */
    private void saveTokenToRedis(Long userId, String userName, String accessToken, String refreshToken) {
        // 1. 保存用户登录信息（Hash结构）
        String userKey = "user:login:" + userId;
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("accessToken", accessToken);
        loginInfo.put("refreshToken", refreshToken);
        loginInfo.put("loginTime", LocalDateTime.now().toString());
        loginInfo.put("lastActiveTime", LocalDateTime.now().toString());
        loginInfo.put("userName", userName);
        
        redisUtil.hSetAll(userKey, loginInfo);
        redisUtil.expire(userKey, jwtProperties.getRefreshTtl(), TimeUnit.MILLISECONDS);

        // 2. 保存 Refresh Token 映射（String结构）
        String tokenKey = "token:refresh:" + refreshToken;
        redisUtil.set(tokenKey, userId, jwtProperties.getRefreshTtl(), TimeUnit.MILLISECONDS);

        log.info("用户 {} 的 Token 已保存到 Redis", userId);
    }
    
    @Override
    public void logout(Long userId) {
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");
        
        try {
            // 1. 获取用户登录信息
            String userKey = "user:login:" + userId;
            
            // 2. 获取 Refresh Token 并删除其映射
            Object refreshTokenObj = redisUtil.hGet(userKey, "refreshToken");
            if (refreshTokenObj != null) {
                String tokenKey = "token:refresh:" + refreshTokenObj.toString();
                redisUtil.delete(tokenKey);
                log.info("已删除用户 {} 的 Refresh Token 映射", userId);
            }
            
            // 3. 删除用户登录信息
            redisUtil.delete(userKey);
            
            log.info("用户 {} 退出登录成功", userId);
        } catch (Exception e) {
            log.error("用户 {} 退出登录失败: {}", userId, e.getMessage());
            throw new BusinessException("退出登录失败: " + e.getMessage());
        }
    }
}
