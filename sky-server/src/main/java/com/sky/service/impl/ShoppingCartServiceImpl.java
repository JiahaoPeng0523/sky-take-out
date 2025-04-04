package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.service.ShoppingCartService;
import com.sky.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        // 判断购物车内是否存在该商品，对比四个字段，以区分不同用户购物车内的不同口味菜品或套餐
        Long userCurrentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCartEle = this.lambdaQuery()
                .eq(ShoppingCart::getUserId, userCurrentId)
                .eq(shoppingCartDTO.getDishId()!=null, ShoppingCart::getDishId, shoppingCartDTO.getDishId())
                .eq(shoppingCartDTO.getSetmealId()!=null, ShoppingCart::getSetmealId, shoppingCartDTO.getSetmealId())
                .eq(shoppingCartDTO.getDishFlavor()!=null, ShoppingCart::getDishFlavor, shoppingCartDTO.getDishFlavor())
                .one();

        // 若商品与口味信息完全一致，则将该商品number字段+1
        if(shoppingCartEle!=null){
            this.lambdaUpdate()
                    .eq(ShoppingCart::getUserId, userCurrentId)
                    .eq(shoppingCartDTO.getDishId()!=null, ShoppingCart::getDishId, shoppingCartDTO.getDishId())
                    .eq(shoppingCartDTO.getSetmealId()!=null, ShoppingCart::getSetmealId, shoppingCartDTO.getSetmealId())
                    .eq(shoppingCartDTO.getDishFlavor()!=null, ShoppingCart::getDishFlavor, shoppingCartDTO.getDishFlavor())
                    .set(ShoppingCart::getNumber, shoppingCartEle.getNumber()+1)
                    .update();
            return;
        }

        // 若不存在该商品，则向购物车内加入该商品
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // 判断当前商品为菜品还是套餐，并查询补全相关属性
        if(shoppingCart.getDishId()!=null){
            // 当前商品为菜品
            Dish dishOne = dishService.lambdaQuery()
                    .eq(Dish::getId, shoppingCart.getDishId())
                    .one();
            shoppingCart.setName(dishOne.getName());
            shoppingCart.setAmount(dishOne.getPrice());
            shoppingCart.setImage(dishOne.getImage());
        }else {
            // 当前商品为套餐
            Setmeal setmealOne = setmealService.lambdaQuery()
                    .eq(Setmeal::getId, shoppingCart.getSetmealId())
                    .one();
            shoppingCart.setName(setmealOne.getName());
            shoppingCart.setAmount(setmealOne.getPrice());
            shoppingCart.setImage(setmealOne.getImage());
        }
        shoppingCart.setNumber(1);
        shoppingCart.setUserId(userCurrentId);
        shoppingCart.setCreateTime(LocalDateTime.now());
        this.save(shoppingCart);
    }

    /**
     * 移除用户购物车中选中的一个商品
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        // 判断当前商品在该用户购物车下有几个
        ShoppingCart one = this.lambdaQuery()
                .eq(ShoppingCart::getUserId, userId)
                .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId())
                .eq(shoppingCart.getDishFlavor() != null, ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor())
                .one();

        // 若剩余两个及以上数量-1
        if(one!=null&&one.getNumber()>=2){
            this.lambdaUpdate()
                    .eq(ShoppingCart::getUserId, userId)
                    .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                    .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId())
                    .eq(shoppingCart.getDishFlavor() != null, ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor())
                    .set(ShoppingCart::getNumber, one.getNumber()-1)
                    .update();
            return;
        }
        // 若剩余一个则在表中移除该数据
        if(one!=null&&one.getNumber()==1){
            this.removeById(one.getId());
            return;
        }
    }

    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> list = this.lambdaQuery()
                .eq(ShoppingCart::getUserId, userId)
                .list();
        return list;
    }

    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, userId);
        this.remove(wrapper);
    }
}
