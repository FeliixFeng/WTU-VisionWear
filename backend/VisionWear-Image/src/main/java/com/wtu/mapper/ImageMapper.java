package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.Image;
import org.apache.ibatis.annotations.Mapper;

/**
 * 连接数据库的mapper
 */
@Mapper
public interface ImageMapper extends BaseMapper<Image> {
}