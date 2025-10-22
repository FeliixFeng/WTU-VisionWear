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
 * 帖子标签关联实体类
 *
 * @author WTU
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("post_tag")
@Schema(description = "帖子标签关联实体")
public class PostTag implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "帖子ID")
    private Long postId;

    @Schema(description = "标签ID")
    private Long tagId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

