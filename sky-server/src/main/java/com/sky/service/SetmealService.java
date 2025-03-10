package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.vo.PageResult;
import com.sky.vo.SetmealVO;
import org.springframework.stereotype.Service;

public interface SetmealService extends IService<Setmeal> {
    boolean saveNewSetmeal(SetmealDTO setmealDTO);

    PageResult<SetmealVO> getSetmealPage(Integer page, Integer pageSize, String name, Long categoryId, Integer status);

    void putSetmeal(SetmealDTO setmealDTO);
}
