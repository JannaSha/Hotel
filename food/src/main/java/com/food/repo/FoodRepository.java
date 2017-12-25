package com.food.repo;

import com.food.models.Food;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;


import java.util.List;

public interface FoodRepository extends CrudRepository<Food, Long> {
    public Food findByName(String name);
    public List<Food> findAll(Pageable pageable);
}
