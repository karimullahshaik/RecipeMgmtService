package com.abnamro.recipes.test.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.abnamor.recipes.dto.Ingredient;
import com.abnamor.recipes.dto.Recipe;
import com.abnamro.recipes.dao.RecipeEntity;
import com.abnamro.recipes.util.Util;

import lombok.extern.slf4j.Slf4j;

//Class to contain common test utility methods
@Slf4j
public class TestUtil {
	
	//Build and return list of ingredients
	public static List<Ingredient> buildIngredients(){
		List<Ingredient> ingredientsList = new ArrayList<>();
		
		Ingredient ing = new Ingredient("ing-1","1 tbsp");ingredientsList.add(ing);
		ing = new Ingredient("ing-2","2 nos");ingredientsList.add(ing);
		ing = new Ingredient("ing-3","3 kgs");ingredientsList.add(ing);
		ing = new Ingredient("ing-4","4 ml");ingredientsList.add(ing);
		log.info("Number of ingredients added to list: "+ingredientsList.size());
		return ingredientsList;
	}
	
	//Build and return instructions as String
	public static String buildInstructions() {
		String instructions = "1. Step-1: \n2. Step-2: \n3. Step-3: \n 4. Step-4: \n";
		
		return instructions;
	}
	
	//Build sample recipe entity using utility methods and return
	public static RecipeEntity buildSampleRecipeEntity(Integer id,String name,String type,Integer capacity) {
		RecipeEntity newRecipe = new RecipeEntity();
		newRecipe.setId(id);
		newRecipe.setName(name);
		newRecipe.setType(type);
		newRecipe.setServingCapacity(capacity);
		newRecipe.setIngredients(Util.convertToJSONString(TestUtil.buildIngredients()));
		Optional<Date> currentDateTime = Util.getCurrentDateTime();
		newRecipe.setCreationDateTime(currentDateTime.isPresent()?currentDateTime.get():null);
		newRecipe.setInstructions(TestUtil.buildInstructions());
		return newRecipe;
	}
	
	//Build sample recipe instance using utility methods and return
	public static Recipe buildSampleRecipe(Integer id,String name,String type,Integer capacity) {
		Recipe newRecipe = new Recipe();
		newRecipe.setId(id);
		newRecipe.setName(name);
		newRecipe.setType(type);
		newRecipe.setServingCapacity(capacity);
		newRecipe.setIngredientsList(TestUtil.buildIngredients());
		Optional<Date> currentDateTime = Util.getCurrentDateTime();
		newRecipe.setCreationDateTime(currentDateTime.isPresent()?currentDateTime.get():null);
		newRecipe.setInstructions(TestUtil.buildInstructions());
		return newRecipe;
	}
}
