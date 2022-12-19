package com.abnamro.recipes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abnamor.recipes.dto.Recipe;
import com.abnamor.recipes.dto.User;
import com.abnamro.recipes.exceptions.BadRequestException;
import com.abnamro.recipes.exceptions.ErrorMessages;
import com.abnamro.recipes.exceptions.NoSuchRecipeFoundException;
import com.abnamro.recipes.exceptions.RecipeNotCreatedException;
import com.abnamro.recipes.exceptions.ResourceConflictException;
import com.abnamro.recipes.exceptions.UnAuthorizedException;
import com.abnamro.recipes.service.RecipesService;
import com.abnamro.recipes.util.Util;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class RecipesController {
	
	@Value("${recipe.user.name}")
	private String recipeUser;
	
	@Value("${jwt.secret.key}")
	private String secretKey;
	
	@Autowired
	private RecipesService service;
	
	@PostMapping("/authenticate")
	public ResponseEntity<User> authenticateAndAuthorizeUser(@RequestBody User userCredentials){
		log.info("Request received for authentication at /api/autheinticate");
		if(userCredentials.getPassword() == null || userCredentials.getPassword() == null) {
			log.error("Given User Credentials are invalid, throwing BadRequest Exception");
			throw new BadRequestException(ErrorMessages.BAD_REQUEST_MSG);
		} else if(userCredentials.getUserName().compareTo(recipeUser) != 0 ||
				  userCredentials.getPassword().compareTo(secretKey) != 0) {
			log.error("Given user crednetials are wrong, throwing BadRequest Exception");
			throw new BadRequestException(ErrorMessages.BAD_REQUEST_MSG);
		}
		
		String genJwtToken = Util.generateJWTToken(userCredentials.getUserName(),userCredentials.getPassword());
		userCredentials.setPassword(genJwtToken);
		log.info("JWT Token generated using provided secret key and returned through response");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userCredentials);
	}
	
	@PostMapping("/recipe")
	public ResponseEntity<Recipe> createRecipe(
			@RequestHeader(value = "Authorization", required = true)String authHeader,
			@RequestBody Recipe recipe){
		log.info("Processing the request for /api/recipe to create new recipe");
		if(Util.checkJwtTokenValidity(secretKey, authHeader) == false) {
			log.error("Given JWT Token is invalid, throwing Unauthorized Exception");
			throw new UnAuthorizedException(ErrorMessages.UNAUTHORIZED_MSG);
		}else if(Util.checkRecipeValidity(recipe) == false) {
			log.error("Provided recipe instance is not valid, throwing Bad Request Exception");
			throw new BadRequestException(ErrorMessages.BAD_REQUEST_MSG);
		} else if(service.getRecipeFromRepository(recipe.getId()) != null) {
			log.error("Provided recipe is having duplicate Id, thowing Resource Conflict Exception");
			throw new ResourceConflictException(ErrorMessages.RESOURCE_CONFLICT_MSG);
		}else {
			log.debug("Calling service.saveRecipeToRepository to save recipe into DB");
			Recipe savedRecipe = service.saveRecipeToRepository(recipe);
			if(savedRecipe == null) {
				log.error("Service failed to save new recipe into DB");
				throw new RecipeNotCreatedException(ErrorMessages.INTERNAL_SERVER_ERR_MSG);
			}
			
			log.info("Service successfully saved new recipe into DB with recipeId: "+savedRecipe.getId());
			return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe);
		}
	}
	
	@GetMapping("/recipe/{id}")
	public ResponseEntity<Recipe> getRecipe(
			@RequestHeader(value = "Authorization", required = true)String authHeader,
			@PathVariable Integer id) 
	{
		log.info("Processing the request for /api/recipe/id to get existing recipe");
		if(Util.checkJwtTokenValidity(secretKey, authHeader) == false) {
			log.error("Given JWT Token is invalid, throwing Unauthorized Exception");
			throw new UnAuthorizedException(ErrorMessages.UNAUTHORIZED_MSG);
		}
		Recipe recipe = service.getRecipeFromRepository(id);
		if(recipe != null) {
			log.info("Requested recipe with id: "+id+" retrieved from DB");
			return ResponseEntity.status(HttpStatus.OK).body(recipe);
		} else {
			log.error("Requested recipe with id: "+id+" not found in DB");
			throw new NoSuchRecipeFoundException(ErrorMessages.RECIPE_NOT_FOUND_MSG);
		}
	}
	
	@GetMapping("/recipes")
	public ResponseEntity<List<Recipe>> getAllRecipes(
			@RequestHeader(value = "Authorization", required = true)String authHeader){
		log.info("Processing the request for /api/recipes to get all recipes from DB");
		if(Util.checkJwtTokenValidity(secretKey, authHeader) == false) {
			log.error("Given JWT Token is invalid, throwing Unauthorized Exception");
			throw new UnAuthorizedException(ErrorMessages.UNAUTHORIZED_MSG);
		}else {
			log.debug("Calling service.getAllRecipes to retrieve all recipes from DB");
			List<Recipe> recipeList = service.getAllRecipesFromRepository();
			if(recipeList.size() == 0) {
				log.error("No recipes found in DB, throwing RecipeNotFound Exception");
				throw new NoSuchRecipeFoundException(ErrorMessages.RECIPES_NOT_FOUND_MSG);
			}
			
			log.info("Number of recipes retrieved from DB: "+recipeList.size());
			return ResponseEntity.status(HttpStatus.OK).body(recipeList);
		}
	}
	
	@PutMapping("/recipe")
	public ResponseEntity<Recipe> modifyRecipe(
			@RequestHeader(value = "Authorization", required = true)String authHeader,
			@RequestBody Recipe recipe){
		log.info("Processing the request for /api/recipe to modify existing recipe");
		if(Util.checkJwtTokenValidity(secretKey, authHeader) == false) {
			log.error("Given JWT Token is invalid, throwing Unauthorized Exception");
			throw new UnAuthorizedException(ErrorMessages.UNAUTHORIZED_MSG);
		}else if(Util.checkRecipeValidity(recipe) == false) {
			log.error("Provided recipe instance is not valid, throwing Bad Request Exception");
			throw new BadRequestException(ErrorMessages.BAD_REQUEST_MSG);
		} else if(service.getRecipeFromRepository(recipe.getId()) == null) {
			log.error("Provided recipe is not found in DB, throwing Recipe NotFound Exception");
			throw new NoSuchRecipeFoundException(ErrorMessages.RECIPE_NOT_FOUND_MSG);
		}else {
			log.debug("Calling service.modifyRecipeInRepository to update recipe");
			Recipe modifiedRecipe = service.modifyExistingRecipeInRepository(recipe);
			if(modifiedRecipe == null) {
				log.error("Service failed to modify recipe in DB");
				throw new RecipeNotCreatedException(ErrorMessages.INTERNAL_SERVER_ERR_MSG);
			}
			
			log.info("Service successfully modifed existing recipe in DB");
			return ResponseEntity.status(HttpStatus.OK).body(modifiedRecipe);
		}
	}
	
	@DeleteMapping("/recipe/{id}")
	public ResponseEntity<String> deleteRecipe(
			@RequestHeader(value = "Authorization", required = true)String authHeader,
			@PathVariable Integer id){
		log.info("Processing the request for /api/recipe/{id} to delete existing recipe");
		if(Util.checkJwtTokenValidity(secretKey, authHeader) == false) {
			log.error("Given JWT Token is invalid, throwing Unauthorized Exception");
			throw new UnAuthorizedException(ErrorMessages.UNAUTHORIZED_MSG);
		}else if(service.getRecipeFromRepository(id) == null) {
			log.error("Provided recipe is not found in DB, throwing Recipe NotFound Exception");
			throw new NoSuchRecipeFoundException(ErrorMessages.RECIPE_NOT_FOUND_MSG);
		}else {
			log.debug("Calling service.deleteRecipeFromRepository to remove recipe");
			service.deleteRecipeFromRepository(id);
			log.info("Requested recipe deleted from DB");
			return ResponseEntity.status(HttpStatus.OK).body("Requested recipe deleted from DB");
		}
	}
}
