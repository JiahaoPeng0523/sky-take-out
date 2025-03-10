package com.sky.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.Result;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import com.sky.vo.PageResult;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "套餐相关接口")
@RequestMapping("/admin/setmeal")
@RestController
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @ApiOperation("套餐新建")
    @PostMapping
    public Result<SetmealVO> postNewSetmeal(@RequestBody SetmealDTO setmealDTO) {
        boolean success = setmealService.saveNewSetmeal(setmealDTO);
        return success ? Result.success() : Result.error("套餐新增失败");
    }

    @ApiOperation("套餐分页查询")
    @GetMapping("/page")
    public Result<PageResult<SetmealVO>> getSetmealPage(Integer page, Integer pageSize, String name, Long categoryId, Integer status) {
        PageResult<SetmealVO> setmealPage = setmealService.getSetmealPage(page, pageSize, name, categoryId, status);
        return setmealPage != null ? Result.success(setmealPage) : Result.error("套餐分页查询失败");
    }

    @ApiOperation("套餐起售/停售")
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(Long id, @PathVariable Integer status) {
        boolean update = setmealService.lambdaUpdate()
                .eq(Setmeal::getId, id)
                .set(Setmeal::getStatus, status)
                .update();
        return update ? Result.success("success") : Result.error("套餐售卖状态修改异常");
    }

    @ApiOperation("套餐批量删除")
    @DeleteMapping
    @Transactional
    public Result<String> deleteSetmeal(@RequestParam List<Long> ids) {
        // 删除setmeal表中的数据
        setmealService.removeByIds(ids);
        // 删除setmeal_dish中的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<SetmealDish>()
                .in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper);
        return Result.success("success");
    }

    @ApiOperation("套餐根据id回显")
    @GetMapping("/{id}")
    public Result<SetmealVO> getSetmealById(@PathVariable Long id){
        // 查询
        Setmeal setmealById = setmealService.getById(id);
        List<SetmealDish> list = setmealDishService.lambdaQuery()
                .eq(SetmealDish::getSetmealId, id)
                .list();

        // 封装结果
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmealById, setmealVO);
        setmealVO.setSetmealDishes(list);
        return Result.success(setmealVO);
    }

    @ApiOperation("套餐修改")
    @PutMapping
    public Result<String> putSetmeal(@RequestBody SetmealDTO setmealDTO){
        setmealService.putSetmeal(setmealDTO);
        return Result.success();

    }

}
