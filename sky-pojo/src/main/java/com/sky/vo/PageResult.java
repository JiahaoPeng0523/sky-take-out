package com.sky.vo;

import com.sky.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResult<T> {
    private Long total;
    private List<T> records;
}
