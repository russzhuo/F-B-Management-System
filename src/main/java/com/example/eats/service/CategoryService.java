package com.example.eats.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.eats.pojo.Category;

public interface CategoryService extends IService<Category> {

    void remove(Long id);
}
