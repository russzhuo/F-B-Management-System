package com.example.eats.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.eats.dto.DishDto;
import com.example.eats.pojo.Dish;
import com.example.eats.pojo.DishFlavor;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
