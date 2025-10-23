package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签Mapper接口
 *
 * @author WTU
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}

