package com.example.eats.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.eats.dto.SetmealDto;
import com.example.eats.pojo.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);

}
