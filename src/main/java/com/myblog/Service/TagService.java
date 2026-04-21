package com.myblog.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myblog.pojo.Tag;

import java.util.List;

public interface TagService extends IService<Tag> {
    Tag getByTagName(String tagName);

}
