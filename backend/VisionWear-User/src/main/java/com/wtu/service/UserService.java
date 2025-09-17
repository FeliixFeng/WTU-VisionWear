package com.wtu.service;
import com.wtu.dto.user.ChangeInfoDTO;
import com.wtu.dto.user.ChangePasswordDTO;
import com.wtu.entity.User;

public interface UserService {
    /**
     * 更改用户密码
     */
    void changePassword(ChangePasswordDTO loginDTO);
    
    /**
     * 修改用户信息
     * @param changeInfoDTO 用户信息DTO
     * @param userId 用户ID
     */
    void changeUserInfo(ChangeInfoDTO changeInfoDTO, Long userId);
    
    /**
     * 根据用户ID获取用户所有信息
     * @param userId 用户ID
     * @return 用户实体对象
     */
    User getUserById(Long userId);
}