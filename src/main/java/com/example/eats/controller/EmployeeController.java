package com.example.eats.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.eats.common.Result;
import com.example.eats.pojo.Employee;
import com.example.eats.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.PushBuilder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CacheManager cacheManager;

    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp=employeeService.getOne(queryWrapper);

        if(emp==null){
            return Result.error("Failed to login");
        }

        if(!emp.getPassword().equals(password)){
            return Result.error("Password does not match");
        }

        if(emp.getStatus()==0){
            //this account is disabled
            return Result.error("this account is disabled");

        }

        request.getSession().setAttribute("employee",emp.getId());
        log.info("logged in");

        return Result.success(emp);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return Result.success("logged out");
    }

    @PostMapping
    public Result<String> addNewEmployee(HttpServletRequest request,@RequestBody Employee employee){
        employee.setPassword((DigestUtils.md5DigestAsHex("123456".getBytes())));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        Long empId = (Long)request.getSession().getAttribute("employee");

//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return Result.success("New employee has been created");
    }

    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize, String name){
        log.info("page={}, pageSize={}, name={}",page,pageSize,name);

        Page pageInfo = new Page(page,pageSize);

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);

        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo,queryWrapper);

        return Result.success(pageInfo);

    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("get employee information by ID");

        Employee employee = employeeService.getById(id);

        if(employee !=null){
            return Result.success(employee);

        }

        return Result.error("employee not found");

    }

    @PutMapping
    public Result<String> update(HttpServletRequest request,@RequestBody Employee employee ){
        log.info(employee.toString());
        Long empId= (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        boolean isUpdated = employeeService.updateById(employee);

        long id = Thread.currentThread().getId();
        log.info("tid: {}",id);

        if(!isUpdated)
            return Result.error("Update employee information unsuccessfully");

        return Result.success("Employee information successfully updated");
    }
}
