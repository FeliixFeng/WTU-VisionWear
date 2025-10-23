package com.wtu.service;

import com.wtu.dto.PostCreateDTO;
import com.wtu.dto.PostUpdateDTO;
import com.wtu.vo.PostVO;

/**
 * 帖子服务接口
 *
 * @author WTU
 */
public interface PostService {

    /**
     * 创建帖子
     *
     * @param dto 创建帖子DTO
     * @return 帖子ID
     */
    Long createPost(PostCreateDTO dto);

    /**
     * 获取帖子详情
     *
     * @param postId 帖子ID
     * @return 帖子详情
     */
    PostVO getPostById(Long postId);

    /**
     * 更新帖子
     *
     * @param postId 帖子ID
     * @param dto    更新帖子DTO
     */
    void updatePost(Long postId, PostUpdateDTO dto);

    /**
     * 删除帖子（软删除）
     *
     * @param postId 帖子ID
     */
    void deletePost(Long postId);
}

