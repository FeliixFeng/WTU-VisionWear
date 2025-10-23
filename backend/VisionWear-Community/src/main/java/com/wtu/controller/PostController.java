package com.wtu.controller;

import com.wtu.dto.PostCreateDTO;
import com.wtu.dto.PostUpdateDTO;
import com.wtu.result.Result;
import com.wtu.service.PostService;
import com.wtu.vo.PostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 帖子控制器
 *
 * @author WTU
 */
@RestController
@RequestMapping("/community")
@Tag(name = "社区模块")
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 创建帖子
     */
    @PostMapping("/post")
    @Operation(summary = "创建帖子")
    public Result<Long> createPost(@Valid @RequestBody PostCreateDTO dto) {
        log.info("创建帖子，参数: {}", dto);
        Long postId = postService.createPost(dto);
        return Result.success(postId, "创建帖子成功");
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/post/{id}")
    @Operation(summary = "获取帖子详情")
    public Result<PostVO> getPostById(@PathVariable("id") Long postId) {
        log.info("获取帖子详情，帖子ID: {}", postId);
        PostVO postVO = postService.getPostById(postId);
        return Result.success(postVO);
    }

    /**
     * 更新帖子
     */
    @PutMapping("/post/{id}")
    @Operation(summary = "更新帖子")
    public Result<String> updatePost(@PathVariable("id") Long postId,
                                      @Valid @RequestBody PostUpdateDTO dto) {
        log.info("更新帖子，帖子ID: {}，参数: {}", postId, dto);
        postService.updatePost(postId, dto);
        return Result.success("更新帖子成功");
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/post/{id}")
    @Operation(summary = "删除帖子")
    public Result<String> deletePost(@PathVariable("id") Long postId) {
        log.info("删除帖子，帖子ID: {}", postId);
        postService.deletePost(postId);
        return Result.success("删除帖子成功");
    }
}

