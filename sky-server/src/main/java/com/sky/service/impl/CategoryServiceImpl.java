package com.sky.service.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
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
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

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

    @Override
    public Result<String> removeCategory(Long id) {
        /* 查询分类下是否还包含菜品或套餐 */
        LambdaQueryWrapper<Dish> lambdaQueryWrapperDish = new LambdaQueryWrapper<Dish>()
                .eq(Dish::getCategoryId, id);
        Integer dishByIdCount = dishMapper.selectCount(lambdaQueryWrapperDish);
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapperDishSetmeal = new LambdaQueryWrapper<Setmeal>()
                .eq(Setmeal::getCategoryId, id);
        Integer setmealByIdCount = setmealMapper.selectCount(lambdaQueryWrapperDishSetmeal);

        if (dishByIdCount == 0 && setmealByIdCount == 0) {
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>()
                    .eq(Category::getId, id);
            boolean remove = remove(wrapper);
            return remove ? Result.success("success") : Result.error("error");
        }
        return Result.error("无法删除分类，其中仍包含套餐或菜品");

    }

    @Override
    public List<Category> list(Integer type) {
        List<Category> list = this.lambdaQuery()
                .eq(type!=null, Category::getType, type)
                .list();
        return list;
    }
}
