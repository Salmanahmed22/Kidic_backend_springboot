package com.example.kidic.repository;

import com.example.kidic.entity.Meal;
import com.example.kidic.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    
    List<Meal> findByChild(Child child);
    
    @Query("SELECT m FROM Meal m WHERE m.title LIKE %:title%")
    List<Meal> findByTitleContaining(@Param("title") String title);
    
    @Query("SELECT m FROM Meal m WHERE m.child.id = :childId")
    List<Meal> findByChildId(@Param("childId") Long childId);
    
    @Query("SELECT m FROM Meal m WHERE m.recipe LIKE %:recipe%")
    List<Meal> findByRecipeContaining(@Param("recipe") String recipe);
    
    @Query("SELECT m FROM Meal m WHERE m.ingredients LIKE %:ingredient%")
    List<Meal> findByIngredientContaining(@Param("ingredient") String ingredient);
    
    long countByChild(Child child);
}
