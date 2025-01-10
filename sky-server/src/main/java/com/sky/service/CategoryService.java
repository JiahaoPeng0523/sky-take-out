package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.vo.PageResult;

public interface CategoryService extends IService<Category> {
    Result<PageResult<Category>> pageQuery(Integer page, Integer pageSize, String name, Integer type);
}
