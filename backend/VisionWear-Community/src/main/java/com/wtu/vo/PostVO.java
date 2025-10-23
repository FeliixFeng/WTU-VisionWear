package com.wtu.vo;

import com.wtu.entity.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子视图对象
 *
 * @author WTU
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "帖子视图对象")
public class PostVO {

    @Schema(description = "帖子ID")
    private Long postId;

    @Schema(description = "发帖用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子内容")
    private String content;

    @Schema(description = "封面图片URL")
    private String coverImage;

    @Schema(description = "帖子类型：1-图文 2-纯文字 3-图片分享")
    private Integer postType;

    @Schema(description = "浏览量")
    private Integer viewCount;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "收藏数")
    private Integer collectCount;

    @Schema(description = "分享数")
    private Integer shareCount;

    @Schema(description = "状态：0-正常 1-删除 2-审核中 3-违规")
    private Integer status;

    @Schema(description = "是否置顶：0-否 1-是")
    private Integer isTop;

    @Schema(description = "是否热门：0-否 1-是")
    private Integer isHot;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "图片URL列表")
    private List<String> images;

    @Schema(description = "标签列表")
    private List<Tag> tags;

    @Schema(description = "当前用户是否点赞")
    private Boolean isLiked;

    @Schema(description = "当前用户是否收藏")
    private Boolean isCollected;
}

