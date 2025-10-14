package com.wtu.controller;
import com.wtu.client.ImageClient;
import com.wtu.dto.user.ChangeInfoDTO;
import com.wtu.dto.user.ChangePasswordDTO;
import com.wtu.entity.User;
import com.wtu.result.Result;
import com.wtu.service.UserService;
import com.wtu.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户模块")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final ImageClient imageClient;

/*    private final MaterialService materialService;*/
    private final UserService userService;

    @GetMapping(value = "/getAllImage")
    @Operation(summary = "获取用户生成的所有图片URL")
    public Result<List<String>> getAllImage() {
        //从token 获取当前用户ID
        Long userId = UserContext.getCurrentUserId();

        // 获取用户生成的所有图片URL
        List<String> imageUrls = imageClient.getAllImageUrls(userId).getData();
        return Result.success(imageUrls);
    }

/*    @GetMapping("/getMaterial")
    @Operation(summary = "获取素材库的图片")
    public Result<List<String>> getMaterial(){
        return Result.success(materialService.getMaterial());
    }*/

    @PostMapping("/changePassword")
    @Operation(summary = "修改用户密码")
    public Result<String> changePassword(@Valid @RequestBody ChangePasswordDTO loginDTO) {
       userService.changePassword(loginDTO);
       return Result.success("修改成功!");
    }
    
    @PostMapping("/changeInfo")
    @Operation(summary = "修改用户信息")
    public Result<String> changeInfo(@Valid @RequestBody ChangeInfoDTO changeInfoDTO) {
        //从token获取当前用户ID
        Long userId = UserContext.getCurrentUserId();
        userService.changeUserInfo(changeInfoDTO, userId);
        return Result.success("用户信息修改成功!");
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "根据ID获取用户所有信息")
    public Result<User> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return Result.success(user);
    }
}