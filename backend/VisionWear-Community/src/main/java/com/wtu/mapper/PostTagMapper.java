package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.PostTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 帖子标签Mapper接口
 *
 * @author WTU
 */
@Mapper
public interface PostTagMapper extends BaseMapper<PostTag> {
}

