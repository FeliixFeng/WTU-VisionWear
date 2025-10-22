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
 * 收藏记录实体类
 *
 * @author WTU
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("collect_record")
@Schema(description = "收藏记录实体")
public class CollectRecord implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "收藏用户ID")
    private Long userId;

    @Schema(description = "帖子ID")
    private Long postId;

    @Schema(description = "收藏夹ID，0表示默认收藏夹")
    private Long folderId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

