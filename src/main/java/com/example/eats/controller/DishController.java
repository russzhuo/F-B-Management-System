package com.example.eats.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.eats.common.Result;
import com.example.eats.dto.DishDto;
import com.example.eats.pojo.Category;
import com.example.eats.pojo.Dish;
import com.example.eats.pojo.DishFlavor;
import com.example.eats.service.CategoryService;
import com.example.eats.service.DishFlavorService;
import com.example.eats.service.DishService;
import jakarta.servlet.http.PushBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {

        dishService.saveWithFlavor(dishDto);

        return Result.success("Dish has been added");
    }

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize,String name){
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name!=null,Dish::getName,name);

        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> dishes = pageInfo.getRecords();

        List<DishDto> list=new ArrayList<>();

        for (Dish d:dishes){
            DishDto dishDto=new DishDto();

            BeanUtils.copyProperties(d,dishDto);

            Long categoryId =  d.getCategoryId();
            Category category=categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            list.add(dishDto);
        }

        dishDtoPage.setRecords(list);
        return Result.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id){
        DishDto dishDto=null;

        String key= "dishdto_"+id;

        dishDto = (DishDto) redisTemplate.opsForValue().get(key);

        if(dishDto !=null){
            return Result.success(dishDto);
        }

        dishDto = dishService.getByIdWithFlavor(id);
        redisTemplate.opsForValue().set(key,dishDto,60,TimeUnit.MINUTES);

        return Result.success(dishDto);
    }

    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        //Clear all dish cache data
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);

        //Clear cache data of certain dish
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);


        return Result.success("dish has been updated");
    }

    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList=null;
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus(); //dish_<category_id>_<status>

        //try retrieving requested data from redis cache
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        //If it exists, return it directly without querying the database
        if(dishDtoList!=null){
            log.info("The requested data exists in redis cache.");
            return Result.success(dishDtoList);
        }

        //If it doesn't exist, query the database and cache the retrieved dish data in Redis
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());

        //see if the status is ok
        queryWrapper.eq(Dish::getStatus,1);

        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> dishes  = dishService.list(queryWrapper);

        dishDtoList=dishes.stream().map((item)->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId=item.getCategoryId();

            // Retrieve the category object based on the ID.
            Category category = categoryService.getById(categoryId);

            if(category!=null){
                String categoryName=category.getName();
                dishDto.setCategoryName(categoryName);
            }

            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);

            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList=dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return Result.success(dishDtoList);
    }
}
