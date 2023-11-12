package com.example.eats.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.eats.common.Result;
import com.example.eats.pojo.Category;
import com.example.eats.service.CategoryService;
import com.example.eats.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<String> save(@RequestBody Category category){
        log.info("category: {}", category);
        categoryService.save(category);
        return Result.success("new category has been created");
    }

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize){
        Page<Category> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo,queryWrapper);

        return Result.success(pageInfo);

    }

    @DeleteMapping
    public Result<String> delete(Long id){
        log.info("Delete category for id: {}", id);
        categoryService.remove(id);
        return Result.success("This category has been deleted");
    }

    @PutMapping
    public Result<String> update(@RequestBody Category category){
        log.info("Update category information: {}",category);
        categoryService.updateById(category);

        return Result.success("The current category information has been successfully updated");

    }

    @GetMapping("/list")
    public Result<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());

        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return Result.success(list);
    }
}
