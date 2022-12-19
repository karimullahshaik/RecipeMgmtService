package com.abnamro.recipes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

//Class to represent not found recipe exception
public class NoSuchRecipeFoundException extends ResponseStatusException {

	public NoSuchRecipeFoundException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}
}
