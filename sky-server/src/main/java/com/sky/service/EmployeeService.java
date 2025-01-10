package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.Result;
import com.sky.vo.PageResult;

public interface EmployeeService extends IService<Employee> {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    Boolean insertNew(EmployeeDTO employeeDTO);

    Result<PageResult> pageOfEmployee(Integer page, Integer pageSize, String name);

    Result<String> updateByIdMy(PasswordEditDTO passwordEditDTO);
}
