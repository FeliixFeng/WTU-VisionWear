package com.wtu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论实体类
 *
 * @author WTU
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("comment")
@Schema(description = "评论实体")
public class Comment implements Serializable {

    @TableId(value = "comment_id", type = IdType.AUTO)
    @Schema(description = "评论ID")
    private Long commentId;

    @Schema(description = "帖子ID")
    private Long postId;

    @Schema(description = "评论用户ID")
    private Long userId;

    @Schema(description = "父评论ID，0表示一级评论")
    private Long parentId;

    @Schema(description = "回复的用户ID")
    private Long replyToUserId;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "状态：0-正常 1-删除 2-审核中")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

