package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 菜品分页查询
     * @param categoryId
     * @param name
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult getPageObject(Integer categoryId, String name, Integer page, Integer pageSize, Integer status) {
        // 设置分页参数
        PageHelper.startPage(page, pageSize);
        // 执行查询
        List<DishVO> dishList = dishMapper.List(categoryId, name, status);
        Page<DishVO> p = (Page<DishVO>) dishList;
        // 封装Dish对象
        PageResult pageResult = new PageResult(p.getTotal(), p.getResult());
        return pageResult;
    }

    /**
     *
     * @param dishDTO
     * @return
     */
    @Override
    public void saveNewDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 保存菜品信息
        save(dish);
        // 保存口味信息
        Dish dishObject = lambdaQuery()
                .eq(Dish::getName, dishDTO.getName())
                .one();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishObject.getId());
            dishFlavorMapper.insert(flavor);
        }
    }

    @Override
    public DishVO getDish(Long id) {
        // 查询菜品信息
        Dish dishById = getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dishById, dishVO);
        // 查询口味信息
        List<DishFlavor> byDishId = dishFlavorMapper.getByDishId(dishVO.getId());
        dishVO.setFlavors(byDishId);
        return dishVO;
    }

    @Override
    public void updateDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 更新菜品信息
        LambdaUpdateWrapper<Dish> wrapper = new LambdaUpdateWrapper<Dish>()
                .eq(Dish::getId, dish.getId());
        update(dish, wrapper);
        // 更新口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<DishFlavor>()
                .eq(DishFlavor::getDishId, dish.getId());
        dishFlavorMapper.delete(queryWrapper);
        for (DishFlavor flavor : dishDTO.getFlavors()) {
            flavor.setDishId(dishDTO.getId());
            dishFlavorMapper.insert(flavor);
        }
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
//        List<Dish> dishList = dishMapper.list(dish);
        List<Dish> dishList = this.lambdaQuery()
                .eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .eq(dish.getStatus() != null, Dish::getStatus, dish.getStatus())
                .list();

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
