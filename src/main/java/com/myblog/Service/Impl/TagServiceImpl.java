package com.myblog.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.Mapper.TagMapper;
import com.myblog.Service.TagService;
import com.myblog.pojo.Tag;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Override
    public Tag getByTagName(String tagName) {
        return lambdaQuery()
                .eq(Tag::getTagName,tagName)
                .one();
    }
}
