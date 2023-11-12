package com.example.eats.dto;

import com.example.eats.pojo.Setmeal;
import com.example.eats.pojo.SetmealDish;
import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
