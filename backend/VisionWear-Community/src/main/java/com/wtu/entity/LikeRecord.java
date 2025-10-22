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
 * 点赞记录实体类
 *
 * @author WTU
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("like_record")
@Schema(description = "点赞记录实体")
public class LikeRecord implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "点赞用户ID")
    private Long userId;

    @Schema(description = "目标ID（帖子或评论）")
    private Long targetId;

    @Schema(description = "目标类型：1-帖子 2-评论")
    private Integer targetType;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

