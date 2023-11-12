package com.example.eats.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.eats.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
