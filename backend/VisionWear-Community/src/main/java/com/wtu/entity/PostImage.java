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
 * 帖子图片关联实体类
 *
 * @author WTU
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("post_image")
@Schema(description = "帖子图片关联实体")
public class PostImage implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "帖子ID")
    private Long postId;

    @Schema(description = "图片ID（关联image表）")
    private Long imageId;

    @Schema(description = "图片URL")
    private String imageUrl;

    @Schema(description = "排序顺序")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

