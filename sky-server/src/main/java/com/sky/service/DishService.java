package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

public interface DishService extends IService<Dish> {
    PageResult getPageObject(Integer categoryId, String name, Integer page, Integer pageSize, Integer status);

    void saveNewDish(DishDTO dishDTO);

    DishVO getDish(Long id);

    void updateDish(DishDTO dishDTO);
}
