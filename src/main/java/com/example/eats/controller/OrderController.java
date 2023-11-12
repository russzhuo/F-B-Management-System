package com.example.eats.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.eats.common.Result;
import com.example.eats.pojo.Dish;
import com.example.eats.pojo.Orders;
import com.example.eats.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("page")
    public Result<Page> page(int page, int pageSize, Long number, String beginTime, String endTime){

        Page<Orders> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        queryWrapper.like(number!=null,Orders::getId,number);

        if(beginTime!=null && endTime!=null){
            LocalDateTime beginTimeLocal = LocalDateTime.parse(beginTime, formatter);

            LocalDateTime endTimeLocal = LocalDateTime.parse(endTime, formatter);

            queryWrapper.between(Orders::getOrderTime,beginTimeLocal,endTimeLocal);
        }


        orderService.page(pageInfo,queryWrapper);
        return Result.success(pageInfo);
    }
}
