package com.example.eats.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eats.common.CustomException;
import com.example.eats.mapper.CategoryMapper;
import com.example.eats.pojo.Category;
import com.example.eats.pojo.Dish;
import com.example.eats.pojo.Setmeal;
import com.example.eats.service.CategoryService;
import com.example.eats.service.DishService;
import com.example.eats.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    @Override
    public void remove(Long id){
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        int count_1= (int) dishService.count(dishLambdaQueryWrapper);

        // See if the current category is associated with any dishes. If it is already associated, throw a business exception.
        if(count_1>0){
            //It is already associated with one or more dishes, so throws a business exception
            throw new CustomException("the current category is associated with one or more dishes");

        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count_2 = (int) setmealService.count(setmealLambdaQueryWrapper);

        // See if the current category is associated with any setmeals. If it is already associated, throw a business exception.
        if(count_2>0){
            //It is already associated with one or more setmeals, so throws a business exception
            throw new CustomException("the current category is associated with one or more setmeals");
        }

        super.removeById(id);

    }

}
