package com.example.eats.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eats.common.CustomException;
import com.example.eats.dto.SetmealDto;
import com.example.eats.mapper.SetmealMapper;
import com.example.eats.pojo.Setmeal;
import com.example.eats.pojo.SetmealDish;
import com.example.eats.service.SetmealDishService;
import com.example.eats.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto){
        this.save(setmealDto);

        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();

        for(SetmealDish item:setmealDishList){
            item.setSetmealId(setmealDto.getId());
        }

        setmealDishService.saveBatch(setmealDishList);


    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids){
        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();

        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = (int) this.count(queryWrapper);
        if(count>0){
            throw new CustomException("Setmeal cannot be removed because it is on sale");
        }

        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(lambdaQueryWrapper);




    }

}
