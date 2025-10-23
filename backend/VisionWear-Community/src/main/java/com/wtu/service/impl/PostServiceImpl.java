package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wtu.dto.PostCreateDTO;
import com.wtu.dto.PostUpdateDTO;
import com.wtu.entity.*;
import com.wtu.exception.BusinessException;
import com.wtu.mapper.*;
import com.wtu.service.PostService;
import com.wtu.utils.UserContext;
import com.wtu.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子服务实现类
 *
 * @author WTU
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PostImageMapper postImageMapper;
    private final PostTagMapper postTagMapper;
    private final TagMapper tagMapper;
    private final LikeRecordMapper likeRecordMapper;
    private final CollectRecordMapper collectRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPost(PostCreateDTO dto) {
        // 1. 获取当前登录用户ID
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException("请先登录");
        }

        // 2. 构建帖子对象
        Post post = Post.builder()
                .userId(currentUserId)
                .title(dto.getTitle())
                .content(dto.getContent())
                .coverImage(dto.getCoverImage())
                .postType(dto.getPostType())
                .viewCount(0)
                .likeCount(0)
                .commentCount(0)
                .collectCount(0)
                .shareCount(0)
                .status(0)  // 正常状态
                .isTop(0)   // 非置顶
                .isHot(0)   // 非热门
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 3. 保存帖子
        int result = postMapper.insert(post);
        if (result <= 0) {
            throw new BusinessException("创建帖子失败");
        }

        Long postId = post.getPostId();
        log.info("用户 {} 贴子对象构造完成，帖子ID: {}", currentUserId, postId);

        // 4. 保存帖子图片（如果有）
        if (!CollectionUtils.isEmpty(dto.getImageUrls())) {
            savePostImages(postId, dto.getImageUrls());
        }

        // 5. 保存帖子标签（如果有）
        if (!CollectionUtils.isEmpty(dto.getTagIds())) {
            savePostTags(postId, dto.getTagIds());
        }

        return postId;
    }

    @Override
    public PostVO getPostById(Long postId) {
        // 1. 查询帖子基本信息
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        // 2. 检查帖子状态
        if (post.getStatus() == 1) {
            throw new BusinessException("帖子已删除");
        }

        // 3. 增加浏览量
        post.setViewCount(post.getViewCount() + 1);
        postMapper.updateById(post);

        // 4. 转换为VO
        PostVO vo = convertToVO(post);

        log.info("获取帖子详情成功，帖子ID: {}", postId);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePost(Long postId, PostUpdateDTO dto) {
        // 1. 获取当前登录用户ID
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException("请先登录");
        }

        // 2. 查询帖子
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        // 3. 权限校验：只能修改自己的帖子
        if (!post.getUserId().equals(currentUserId)) {
            throw new BusinessException("无权修改此帖子");
        }

        // 4. 检查帖子状态
        if (post.getStatus() == 1) {
            throw new BusinessException("帖子已删除，无法修改");
        }

        // 5. 更新帖子基本信息
        if (dto.getTitle() != null) {
            post.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            post.setContent(dto.getContent());
        }
        if (dto.getCoverImage() != null) {
            post.setCoverImage(dto.getCoverImage());
        }
        if (dto.getPostType() != null) {
            post.setPostType(dto.getPostType());
        }
        post.setUpdateTime(LocalDateTime.now());

        int result = postMapper.updateById(post);
        if (result <= 0) {
            throw new BusinessException("更新帖子失败");
        }

        // 6. 更新图片列表（如果提供了新的图片列表）
        if (dto.getImageUrls() != null) {
            // 删除旧图片
            postImageMapper.delete(
                    new LambdaQueryWrapper<PostImage>()
                            .eq(PostImage::getPostId, postId)
            );
            // 保存新图片
            if (!dto.getImageUrls().isEmpty()) {
                savePostImages(postId, dto.getImageUrls());
            }
        }

        // 7. 更新标签列表（如果提供了新的标签列表）
        if (dto.getTagIds() != null) {
            // 删除旧标签
            postTagMapper.delete(
                    new LambdaQueryWrapper<PostTag>()
                            .eq(PostTag::getPostId, postId)
            );
            // 保存新标签
            if (!dto.getTagIds().isEmpty()) {
                savePostTags(postId, dto.getTagIds());
            }
        }

        log.info("用户 {} 更新帖子成功，帖子ID: {}", currentUserId, postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long postId) {
        // 1. 获取当前登录用户ID
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException("请先登录");
        }

        // 2. 查询帖子
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        // 3. 权限校验：只能删除自己的帖子
        if (!post.getUserId().equals(currentUserId)) {
            throw new BusinessException("无权删除此帖子");
        }

        // 4. 软删除（更新状态为1）
        post.setStatus(1);
        post.setUpdateTime(LocalDateTime.now());
        int result = postMapper.updateById(post);
        if (result <= 0) {
            throw new BusinessException("删除帖子失败");
        }

        log.info("用户 {} 删除帖子成功，帖子ID: {}", currentUserId, postId);
    }

    /**
     * 保存帖子图片
     */
    private void savePostImages(Long postId, List<String> imageUrls) {
        for (int i = 0; i < imageUrls.size(); i++) {
            PostImage postImage = PostImage.builder()
                    .postId(postId)
                    .imageId(null)  // 如果需要关联 image 表，这里可以传入 imageId
                    .imageUrl(imageUrls.get(i))
                    .sortOrder(i + 1)
                    .createTime(LocalDateTime.now())
                    .build();
            postImageMapper.insert(postImage);
        }
    }

    /**
     * 保存帖子标签
     */
    private void savePostTags(Long postId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            // 验证标签是否存在
            Tag tag = tagMapper.selectById(tagId);
            if (tag == null) {
                log.warn("标签不存在，跳过。标签ID: {}", tagId);
                continue;
            }

            PostTag postTag = PostTag.builder()
                    .postId(postId)
                    .tagId(tagId)
                    .createTime(LocalDateTime.now())
                    .build();
            postTagMapper.insert(postTag);

            // 更新标签使用次数
            tag.setUseCount(tag.getUseCount() + 1);
            tagMapper.updateById(tag);
        }
    }

    /**
     * 将 Post 转换为 PostVO
     */
    private PostVO convertToVO(Post post) {
        PostVO vo = new PostVO();
        BeanUtils.copyProperties(post, vo);

        // 查询帖子图片列表
        List<PostImage> postImages = postImageMapper.selectList(
                new LambdaQueryWrapper<PostImage>()
                        .eq(PostImage::getPostId, post.getPostId())
                        .orderByAsc(PostImage::getSortOrder)
        );
        List<String> imageUrls = postImages.stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());
        vo.setImages(imageUrls);

        // 查询帖子标签列表
        List<Long> tagIds = postTagMapper.selectList(
                new LambdaQueryWrapper<PostTag>()
                        .eq(PostTag::getPostId, post.getPostId())
        ).stream().map(PostTag::getTagId).collect(Collectors.toList());

        if (!tagIds.isEmpty()) {
            List<Tag> tags = tagMapper.selectBatchIds(tagIds);
            vo.setTags(tags);
        } else {
            vo.setTags(new ArrayList<>());
        }

        // TODO: 通过 Feign 调用 User 模块获取用户信息
        // User author = userClient.getUserById(post.getUserId()).getData();
        // vo.setUserName(author.getUserName());
        // vo.setNickName(author.getNickName());
        // vo.setAvatar(author.getAvatar());

        // 暂时设置为 null，等待 Feign 集成
        vo.setUserName(null);
        vo.setNickName(null);
        vo.setAvatar(null);

        // 判断当前用户是否点赞/收藏了该帖子
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId != null) {
            // 查询是否点赞
            boolean isLiked = likeRecordMapper.exists(
                    new LambdaQueryWrapper<LikeRecord>()
                            .eq(LikeRecord::getUserId, currentUserId)
                            .eq(LikeRecord::getTargetId, post.getPostId())
                            .eq(LikeRecord::getTargetType, 1)  // 1-帖子
            );
            vo.setIsLiked(isLiked);

            // 查询是否收藏
            boolean isCollected = collectRecordMapper.exists(
                    new LambdaQueryWrapper<CollectRecord>()
                            .eq(CollectRecord::getUserId, currentUserId)
                            .eq(CollectRecord::getPostId, post.getPostId())
            );
            vo.setIsCollected(isCollected);
        } else {
            vo.setIsLiked(false);
            vo.setIsCollected(false);
        }

        return vo;
    }
}

