package com.abnamro.recipes.exceptions;

//Class to contain customized error messages
public class ErrorMessages {
	public static final String RECIPE_NOT_FOUND_MSG = "Requested recipe not found in DB";
	public static final String RECIPES_NOT_FOUND_MSG = "No recipes found in DB";
	public static final String BAD_REQUEST_MSG = "Bad Request, check request body / parameter type and value";
	public static final String UNAUTHORIZED_MSG = "JWT Token is not authorized to access end point";
	public static final String INTERNAL_SERVER_ERR_MSG = "Unknown error occurred, check the logs for more details";
	public static final String RESOURCE_CONFLICT_MSG = "Recipe Id should be unique to be added to DB";
}
