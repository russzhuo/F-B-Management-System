package com.example.eats.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.eats.common.Result;
import com.example.eats.dto.SetmealDto;
import com.example.eats.pojo.Category;
import com.example.eats.pojo.Setmeal;
import com.example.eats.service.CategoryService;
import com.example.eats.service.SetmealDishService;
import com.example.eats.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto){
        log.info("setmeal info: {}",setmealDto);

        setmealService.saveWithDish(setmealDto);

        return Result.success("setmeal saved");
    }

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper= new LambdaQueryWrapper<>();

        queryWrapper.like(name!=null,Setmeal::getName,name);

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list =new ArrayList<>();

        for(Setmeal setmeal:records){
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);

            Long categoryId=setmeal.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            list.add(setmealDto);
        }
        return Result.success(pageInfo);
    }

    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids){
        log.info("ids: {}",ids);
        setmealService.removeWithDish(ids);

        return Result.success("deleted");
    }

    @GetMapping("/list")
    public Result<List<Setmeal>> list(Setmeal setmeal){

//        String key ="setmeal_"+setmeal.getId()+"_"
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null, Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());

        List<Setmeal> list = setmealService.list(queryWrapper);

        return Result.success(list);

    }
}
