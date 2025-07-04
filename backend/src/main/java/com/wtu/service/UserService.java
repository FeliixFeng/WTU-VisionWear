package com.wtu.service;
import com.wtu.DTO.user.ChangeInfoDTO;
import com.wtu.DTO.user.ChangePasswordDTO;

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
}