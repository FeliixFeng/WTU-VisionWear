package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.Post;
import org.apache.ibatis.annotations.Mapper;

/**
 * 帖子Mapper接口
 *
 * @author WTU
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {
}

