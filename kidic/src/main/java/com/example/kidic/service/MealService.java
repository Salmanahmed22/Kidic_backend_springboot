package com.example.kidic.service;

import com.example.kidic.entity.Child;
import com.example.kidic.entity.Meal;
import com.example.kidic.repository.ChildRepository;
import com.example.kidic.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MealService {

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private ChildRepository childRepository;

    public List<Meal> listForChild(Long childId) {
        return mealRepository.findByChildId(childId);
    }

    public Meal addForChild(Long childId,
                            String title,
                            List<String> ingredients,
                            String recipe) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));

        Meal meal = new Meal();
        meal.setTitle(title);
        meal.setIngredients(ingredients);
        meal.setRecipe(recipe);
        meal.setChild(child);
        return mealRepository.save(meal);
    }

    public Meal editForChild(Long childId,
                             Long mealId,
                             String title,
                             List<String> ingredients,
                             String recipe) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));
        if (!meal.getChild().getId().equals(childId)) {
            throw new IllegalArgumentException("Meal does not belong to child");
        }
        if (title != null) meal.setTitle(title);
        if (ingredients != null) meal.setIngredients(ingredients);
        if (recipe != null) meal.setRecipe(recipe);
        return mealRepository.save(meal);
    }

    public void deleteForChild(Long childId, Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));
        if (!meal.getChild().getId().equals(childId)) {
            throw new IllegalArgumentException("Meal does not belong to child");
        }
        mealRepository.delete(meal);
    }
}


