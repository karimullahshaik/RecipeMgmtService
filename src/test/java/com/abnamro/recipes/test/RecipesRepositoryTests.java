package com.abnamro.recipes.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.abnamor.recipes.dto.Ingredient;
import com.abnamro.recipes.dao.RecipeEntity;
import com.abnamro.recipes.repository.RecipesRepository;
import com.abnamro.recipes.test.util.TestUtil;
import com.abnamro.recipes.util.Util;

@DataJpaTest
public class RecipesRepositoryTests {

	@Autowired
	private RecipesRepository repo;
	
	@Test
	public void SaveAndFindRecipeByIdTest() {
		//Get new sample recipe
		RecipeEntity newRecipe = TestUtil.buildSampleRecipeEntity(101,"Pulav","vg",10);
		//Save new recipe to repository
		repo.save(newRecipe);
		
		//Retrieve new recipe based on id
		Optional<RecipeEntity> optionalRecipe = repo.findById(newRecipe.getId());
		
		//Validate there's recipe instance in optional
		assertThat(optionalRecipe.isPresent()).as("Retrieved recipe is null").isTrue();
		
		//Get returned recipe from Optional
		RecipeEntity retRecipe = optionalRecipe.get();
		
		//validate both recipes are equal
		assertThat(retRecipe).as("Retrieved recipe is not equal to saved recipe").isEqualTo(newRecipe);
		
		//Validate Ingredients present in retrieved recipe
		List<Ingredient> ingredientsList = Util.convertJSONStringToIngredientsList(retRecipe.getIngredients());
		assertThat(ingredientsList).as("Number of ingredients are not matching with expected of 4")
									.isNotEmpty().hasSize(4);
	}

	@Test
	public void SaveAndFindRecipesByCreationDateTimeTest() {
		//Get new sample recipe
		RecipeEntity newRecipe = TestUtil.buildSampleRecipeEntity(102,"Puliyogare","vg",6);
		//Save new recipe to repository
		repo.save(newRecipe);
		
		//Retrieve recipes based on creation date and time
		List<RecipeEntity> recipeEntities = repo.findRecipesByCreationDateTime(newRecipe.getCreationDateTime());
		
		//Validate there's exactly one recipe returned from repository
		assertThat(recipeEntities).as("Recipes in list are not expected as 1")
		                   .hasSize(1);
		
		//validate both recipes are having same creation date time value
		assertThat(newRecipe.getCreationDateTime())
		            .as("Saved recipe and retrieved recipe have different creation date time")
					.isEqualTo(recipeEntities.get(0).getCreationDateTime());
	}
	
	@Test
	public void SaveAndFindRecipesByTypeTest() {
		//Get and Save two recipes into repository with different type
		RecipeEntity newRecipe1 = TestUtil.buildSampleRecipeEntity(103,"Mutton Biriyani","ng",20);
		RecipeEntity newRecipe2 = TestUtil.buildSampleRecipeEntity(104,"Veg Biriyani","vg",10);
		repo.save(newRecipe1);
		repo.save(newRecipe2);
		
		//Retrieve all vegetarian recipes from repository
		List<RecipeEntity> recipeEntities = repo.findRecipesByType("vg");
		
		//Validate all retrieved recipes are of type vg
		assertThat(recipeEntities).extracting(recipe -> recipe.getType())
        					.as("Recipe type is not equal to vg")
        					.contains("vg");
		
		//Retrieve all vegetarian recipes from repository
		recipeEntities = repo.findRecipesByType("ng");

		//Validate all retrieved recipes are of type ng
		assertThat(recipeEntities).extracting(recipe -> recipe.getType())
		                   .as("Recipe type is not equal to ng")
		                   .contains("ng");
	}
	
	@Test
	public void SaveAndFindRecipesByServingCapacityTest() {
		//Get new sample recipe
		RecipeEntity newRecipe = TestUtil.buildSampleRecipeEntity(105,"White Rice","vg",30);
		//Save new recipe to repository
		repo.save(newRecipe);
		
		//Retrieve recipes based on creation date and time
		List<RecipeEntity> recipeEntities = repo.findRecipesByServingCapacity(30);
		
		//Validate there's exactly one recipe returned from repository
		assertThat(recipeEntities).as("Recipes do not contain exactly 1 recipe as expected")
			               .hasSize(1);
		
		//validate both recipes are having same serving capacity value
		assertThat(recipeEntities).extracting(recipe -> recipe.getServingCapacity())
                           .as("Saved recipe and retrieved recipe have different serving capacity")
                           .contains(newRecipe.getServingCapacity());
	}
}
