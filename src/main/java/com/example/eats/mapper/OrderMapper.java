package com.example.eats.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.eats.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
