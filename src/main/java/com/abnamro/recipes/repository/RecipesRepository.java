package com.abnamro.recipes.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abnamro.recipes.dao.RecipeEntity;

@Repository
public interface RecipesRepository extends JpaRepository<RecipeEntity,Integer> {
	//Method to retrieve all recipes from repository matching with given DateTime value
	List<RecipeEntity> findRecipesByCreationDateTime(Date dateTime);
	
	//Method to retrieve all recipes from repository matching with given recipe type
	List<RecipeEntity> findRecipesByType(String type);
	
	//Method to retrieve all recipes from repository matching with given serving capacity
	List<RecipeEntity> findRecipesByServingCapacity(Integer capacity);
	
}
