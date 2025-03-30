package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.PageResult;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealDishService setmealDishService;

    private boolean insertSetmealDish = true;

    @Transactional    //事务
    @Override
    public boolean saveNewSetmeal(SetmealDTO setmealDTO) {
        // setmeal表添加数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        boolean saveSetmeal = save(setmeal);
        // setmeal_dish表添加数据
        for (SetmealDish setmealDish : setmealDTO.getSetmealDishes()) {
            // 查询新增套餐id
            LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<Setmeal>()
                    .eq(Setmeal::getName, setmealDTO.getName());
            Setmeal one = getOne(queryWrapper);
            setmealDish.setSetmealId(one.getId());
            int insert = setmealDishMapper.insert(setmealDish);
            if(insert!=1){
                insertSetmealDish = false;
            }
        }
        return saveSetmeal&&insertSetmealDish;
    }

    @Override
    public PageResult<SetmealVO> getSetmealPage(Integer page, Integer pageSize, String name, Long categoryId, Integer status) {
        PageHelper.startPage(page, pageSize);
        // 执行查询
        List<SetmealVO> setmealVOList = setmealMapper.list(name, categoryId, status);
        Page<SetmealVO> p = (Page<SetmealVO>) setmealVOList;
        // 封装对象
        PageResult<SetmealVO> pageResult = new PageResult<>(p.getTotal(), p.getResult());
        return pageResult;
    }

    @Transactional
    @Override
    public void putSetmeal(SetmealDTO setmealDTO) {
        // 更新套餐信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        updateById(setmeal);
        // 更新套餐菜品关联表
        setmealDTO.getSetmealDishes().stream().forEach(setmealDish -> setmealDish.setSetmealId(setmealDTO.getId()));
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<SetmealDish>()
                .eq(SetmealDish::getSetmealId, setmeal.getId());
        setmealDishService.remove(queryWrapper);
        setmealDishService.saveBatch(setmealDTO.getSetmealDishes());
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.userList(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
