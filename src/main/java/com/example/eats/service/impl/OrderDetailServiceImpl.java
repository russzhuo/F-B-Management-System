package com.example.eats.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eats.mapper.EmployeeMapper;
import com.example.eats.mapper.OrderDetailMapper;
import com.example.eats.pojo.Employee;
import com.example.eats.pojo.OrderDetail;
import com.example.eats.service.EmployeeService;
import com.example.eats.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
