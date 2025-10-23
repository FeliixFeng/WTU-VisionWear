package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.LikeRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 点赞记录Mapper接口
 *
 * @author WTU
 */
@Mapper
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {
}

