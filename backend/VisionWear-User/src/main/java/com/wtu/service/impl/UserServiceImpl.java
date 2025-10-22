package com.wtu.service.impl;


import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wtu.dto.user.ChangeInfoDTO;
import com.wtu.dto.user.ChangePasswordDTO;
import com.wtu.entity.User;
import com.wtu.exception.BusinessException;
import com.wtu.mapper.UserMapper;
import com.wtu.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 更改用户密码
     */
    @Override
    public void changePassword(ChangePasswordDTO loginDTO) {
        LambdaQueryWrapper<User> findWrapper = new LambdaQueryWrapper<>();
        findWrapper.eq(User::getUserName,loginDTO.getUsername()).
        eq(User::getStatus,"0");
        boolean exists = userMapper.exists(findWrapper);
        if(!exists){
            throw new BusinessException("用户名不存在！");
        }


        String oldPassword = DigestUtil.md5Hex(loginDTO.getOldPassword());

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,loginDTO.getUsername()).
                eq(User::getPassWord,oldPassword).
                eq(User::getStatus,"0");

        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            throw new BusinessException("用户名或账号密码错误!");
        }else{
            String newPassword = DigestUtil.md5Hex(loginDTO.getNewPassword());
            user.setPassWord(newPassword);
            userMapper.updateById(user);
        }

    }
    
    /**
     * 修改用户信息
     * @param changeInfoDTO 用户信息DTO
     * @param userId 用户ID
     */
    @Override
    public void changeUserInfo(ChangeInfoDTO changeInfoDTO, Long userId) {
        // 查询用户是否存在
        User user = userMapper.selectById(userId);
        if(user == null){
            throw new BusinessException("用户不存在！");
        }
        
        // 处理用户名
        if (StringUtils.hasText(changeInfoDTO.getUserName())) {
            // 检查用户名长度
            if (changeInfoDTO.getUserName().length() > 20) {
                throw new BusinessException("用户名长度不能超过20个字符");
            }
            
            // 检查用户名是否已存在
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserName, changeInfoDTO.getUserName())
                    .ne(User::getUserId, userId); // 排除当前用户
            if (userMapper.exists(queryWrapper)) {
                throw new BusinessException("该用户名已被使用");
            }
            
            user.setUserName(changeInfoDTO.getUserName());
        }

        //处理昵称
        if(StringUtils.hasText(changeInfoDTO.getNickName())){
            if(changeInfoDTO.getNickName().length()>20){
                throw new BusinessException("昵称长度不能超过20个字符");
            }
            user.setNickName(changeInfoDTO.getNickName());
        }
        
        // 处理邮箱
        if (StringUtils.hasText(changeInfoDTO.getEmail())) {
            user.setEmail(changeInfoDTO.getEmail());
        }
        
        // 处理手机号
        if (StringUtils.hasText(changeInfoDTO.getPhone())) {
            user.setPhone(changeInfoDTO.getPhone());
        }
        
        // 处理头像
        if (StringUtils.hasText(changeInfoDTO.getAvatar())) {
            user.setAvatar(changeInfoDTO.getAvatar());
        }
        
        // 处理生日
        if (StringUtils.hasText(changeInfoDTO.getBirthday())) {
            user.setBirthday(java.time.LocalDate.parse(changeInfoDTO.getBirthday()));
        }

        // 更新用户信息
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException("修改用户信息失败！");
        }
        
        log.info("用户{}信息修改成功", userId);
    }
    
    /**
     * 根据用户ID获取用户所有信息
     * @param userId 用户ID
     * @return 用户实体对象
     */
    @Override
    public User getUserById(Long userId) {
        // 查询用户是否存在
        User user = userMapper.selectById(userId);
        if(user == null){
            throw new BusinessException("用户不存在！");
        }
        return user;
    }
}