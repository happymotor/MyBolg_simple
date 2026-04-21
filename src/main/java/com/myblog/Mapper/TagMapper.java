package com.myblog.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myblog.pojo.Tag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}
