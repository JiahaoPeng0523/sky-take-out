package com.sky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Result<PageResult<Category>> pageQuery(Integer page, Integer pageSize, String name, Integer type) {
        /* 设置分页参数 */
        PageHelper.startPage(page, pageSize);
        /* 执行查询 */
        List<Category> categoryList = categoryMapper.list(name, type);
        Page<Category> p = (Page<Category>) categoryList;
        /* 封装分压查询对象 */
        PageResult<Category> pageResult = new PageResult<>(p.getTotal(), p.getResult());
        return Result.success(pageResult);
    }
}
