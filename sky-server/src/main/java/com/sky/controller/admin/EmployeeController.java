package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import com.sky.vo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@Api(tags = "员工相关接口")
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation("员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation("员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * @param employeeDTO
     * @return
     */
    @ApiOperation("员工新增")
    @PostMapping
    public Result<String> insert(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工信息为：" + employeeDTO);
        return employeeService.insertNew(employeeDTO) ? Result.success("success") : Result.error("error");
    }

    @ApiOperation("员工分页查询")
    @GetMapping("/page")
    public Result<PageResult> selectPage(Integer page, Integer pageSize, String name) {
        return employeeService.pageOfEmployee(page, pageSize, name);
    }

    @ApiOperation("员工账号启用、禁用")
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status, Long id) {
        /* 查询员工 */
        Employee byId = employeeService.getById(id);
        byId.setStatus(status);
        /* 修改员工状态 */
        boolean b = employeeService.updateById(byId);
        return b ? Result.success("success") : Result.error("error");
    }

    @ApiOperation("根据id查询员工")
    @GetMapping("/{id}")
    public Result<Employee> selectById(@PathVariable Long id) {
        Employee byId = employeeService.getById(id);
        return byId != null ? Result.success(byId) : Result.error("error");
    }

    @ApiOperation("修改员工信息")
    @PutMapping
    public Result<String> update(@RequestBody Employee employee) {
        boolean b = employeeService.updateById(employee);
        return b ? Result.success("success") : Result.error("error");
    }

}
