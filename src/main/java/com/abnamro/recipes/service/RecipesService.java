package com.abnamro.recipes.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abnamor.recipes.dto.Ingredient;
import com.abnamor.recipes.dto.Recipe;
import com.abnamro.recipes.dao.RecipeEntity;
import com.abnamro.recipes.repository.RecipesRepository;
import com.abnamro.recipes.util.Util;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RecipesService {
	
	@Autowired
	private RecipesRepository recipesRepo;
	
	//Method to map fields between Recipe and Recipe Entity
	private RecipeEntity mapToRecipeEntity(Recipe recipe) {
		RecipeEntity rEntity = new RecipeEntity();
		//Map primitive fields
		rEntity.setId(recipe.getId());
		rEntity.setName(recipe.getName());
		rEntity.setType(recipe.getType());
		rEntity.setServingCapacity(recipe.getServingCapacity());
		
		//Save current date time into recipe instance, if not given
		if(recipe.getCreationDateTime() == null) {
			Optional<Date> currentDateTime = Util.getCurrentDateTime();
			if(currentDateTime.isPresent())
				log.debug("Current DateTime to be set in recipe entity: "+currentDateTime.toString());
			else
				log.warn("Setting null to current date time field in recipe entity");
			rEntity.setCreationDateTime(currentDateTime.get());
		} else {
			log.debug("Retaining given creation date time value into recipe entity");
			rEntity.setCreationDateTime(recipe.getCreationDateTime());
		}
		
		//Convert ingredients list into String and set to recipe entity
		log.debug("Number of ingredients to convert to string: "+recipe.getIngredientsList().size());
		String ingredients = Util.convertToJSONString(recipe.getIngredientsList());
		log.debug("Ingredients String: "+ingredients);
		rEntity.setIngredients(ingredients);
		
		rEntity.setInstructions(recipe.getInstructions());
		
		return rEntity;
	}
	
	//Method to map fields between Recipe and Recipe Entity
	private Recipe mapToRecipeObject(RecipeEntity recipeEntity) {
		Recipe recipe = new Recipe();
		//Map primitive fields
		recipe.setId(recipeEntity.getId());
		recipe.setName(recipeEntity.getName());
		recipe.setType(recipeEntity.getType());
		recipe.setServingCapacity(recipeEntity.getServingCapacity());
		
		//Format creation date time to required format
		if(recipeEntity.getCreationDateTime() != null)
			recipe.setCDateTimeString(Util.formatDateTime(recipeEntity.getCreationDateTime()));
		recipe.setCreationDateTime(recipeEntity.getCreationDateTime());
		
		//Convert ingredients string into list and set to recipe object 
		log.debug("Ingredients String from DB: "+recipeEntity.getIngredients());
		List<Ingredient> ingredientsList = Util.convertJSONStringToIngredientsList(recipeEntity.getIngredients());
		log.debug("Ingredients List size after conversion: "+ingredientsList.size());
		recipe.setIngredientsList(ingredientsList);

		recipe.setInstructions(recipeEntity.getInstructions());

		return recipe;
	}
	//Method to save given new recipe onto persistence layer
	public Recipe saveRecipeToRepository(Recipe newRecipe) {
		//Map recipe to Recipe Entity
		RecipeEntity recipeEntity = mapToRecipeEntity(newRecipe);
		return mapToRecipeObject(recipesRepo.save(recipeEntity));
	}
	
	//Method to query and retrieve requested recipe based on it's id
	public Recipe getRecipeFromRepository(Integer id) {
		Optional<RecipeEntity> optRecipe = recipesRepo.findById(id);
		if(optRecipe.isPresent())
			return mapToRecipeObject(optRecipe.get());
		else
			return null;
	}
	
	//Method to retrieve all recipes 
	public List<Recipe> getAllRecipesFromRepository(){
		List<RecipeEntity> retrievedRecipes = recipesRepo.findAll();
		log.debug("Number of retrieved recipes from DB: "+retrievedRecipes.size());
		//Map all retrieved recipes entity to recipe instances
		List<Recipe> recipesList = new ArrayList<>(retrievedRecipes.size());
		retrievedRecipes.forEach(recipeEntity -> recipesList.add(mapToRecipeObject(recipeEntity)));
		log.debug("Number of recipe entities mapped and stored to recipesList: "+recipesList.size());
		//Return mapped recipes
		return recipesList;
	}
	
	//Method to modify an existing recipe
	public Recipe modifyExistingRecipeInRepository(Recipe recipe) {
		return saveRecipeToRepository(recipe);
	}
	
	//Method to query and delete requested recipe based on it's id from repository
	public void deleteRecipeFromRepository(Integer id) {
		log.debug("Deleting recipe with id: "+id+" from repository, if it is present");
		recipesRepo.deleteById(id);
		log.debug("Requested recipe should be deleted");
	}
}
