package com.sky.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.vo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "分类相关接口")
@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("分类分页查询")
    @GetMapping("/page")
    public Result<PageResult<Category>> pageQuery(Integer page, Integer pageSize, String name, Integer type) {
        return categoryService.pageQuery(page, pageSize, name, type);
    }

    @ApiOperation("类别新增")
    @PostMapping
    public Result<String> insert(@RequestBody Category category) {
        log.info("新增分类信息：" + category);
        boolean save = categoryService.save(category);
        return save ? Result.success("success") : Result.error("error");
    }

    @ApiOperation("类别禁用")
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status, Long id) {
        boolean update = categoryService.lambdaUpdate()
                .set(Category::getStatus, status)
                .eq(Category::getId, id)
                .update();
        return update ? Result.success("success") : Result.error("error");
    }

    @ApiOperation("类别删除")
    @DeleteMapping
    public Result<String> delete(Long id){
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>()
                .eq(Category::getId, id);
        boolean remove = categoryService.remove(wrapper);
        return remove ? Result.success("success") : Result.error("error");
    }

    @ApiOperation("类别修改")
    @PutMapping
    public Result<String> update(@RequestBody Category category){
        boolean update = categoryService.lambdaUpdate()
                .set(Category::getName, category.getName())
                .set(Category::getSort, category.getSort())
                .eq(Category::getId, category.getId())
                .update();
        return update ? Result.success("success") : Result.error("error");
    }
}
