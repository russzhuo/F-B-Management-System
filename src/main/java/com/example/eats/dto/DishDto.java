package com.example.eats.dto;

import com.example.eats.mapper.DishFlavorMapper;
import com.example.eats.pojo.DishFlavor;
import com.example.eats.pojo.Dish;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish{
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
