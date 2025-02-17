package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.impl.DishServiceImpl;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = "菜品相关接口")
@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishServiceImpl dishService;

    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> getPageObject(Integer categoryId, String name, Integer page, Integer pageSize, Integer status) {
        PageResult pageObject = dishService.getPageObject(categoryId, name, page, pageSize, status);
        return pageObject != null ? Result.success(pageObject) : Result.error("菜品分页查询错误");
    }

    @ApiOperation("菜品批量删除")
    @DeleteMapping
    public Result<String> deleteBatchObject(String ids) {
        String[] idsSplit = ids.split(",");
        List<String> idsList = Arrays.stream(idsSplit).collect(Collectors.toList());
        return dishService.removeByIds(idsList) ? Result.success("success") : Result.error("菜品批量删除错误");
    }
    
    @ApiOperation("菜品停售/起售")
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(Long id, @PathVariable Integer status){
        boolean updateResult = dishService.lambdaUpdate()
                .eq(Dish::getId, id)
                .set(Dish::getStatus, status)
                .update();
        return updateResult? Result.success("success"): Result.error("菜品停售/起售失败");
    }

    @ApiOperation("新增菜品")
    @PostMapping
    public Result<String> saveNewDish(@RequestBody DishDTO dishDTO){
        dishService.saveNewDish(dishDTO);
        return Result.success("success");
    }

    @ApiOperation("根据主键菜品查询")
    @GetMapping("/{id}")
    public Result<DishVO> getDish(@PathVariable Long id){
        DishVO dishVO = dishService.getDish(id);
        return Result.success(dishVO);
    }

    @ApiOperation("修改菜品信息")
    @PutMapping
    public Result<String> updateDish(@RequestBody DishDTO dishDTO){
        dishService.updateDish(dishDTO);
        return Result.success("success");
    }

}
