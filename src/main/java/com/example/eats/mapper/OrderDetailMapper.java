package com.example.eats.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.eats.pojo.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
