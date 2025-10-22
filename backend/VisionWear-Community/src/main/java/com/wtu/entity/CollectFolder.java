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
 * 收藏夹实体类
 *
 * @author WTU
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("collect_folder")
@Schema(description = "收藏夹实体")
public class CollectFolder implements Serializable {

    @TableId(value = "folder_id", type = IdType.AUTO)
    @Schema(description = "收藏夹ID")
    private Long folderId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "收藏夹名称")
    private String folderName;

    @Schema(description = "收藏夹描述")
    private String description;

    @Schema(description = "帖子数量")
    private Integer postCount;

    @Schema(description = "是否公开：0-私密 1-公开")
    private Integer isPublic;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

