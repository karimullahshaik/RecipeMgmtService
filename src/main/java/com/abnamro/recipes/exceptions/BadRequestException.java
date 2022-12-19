package com.abnamro.recipes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

//Class to represent bad request exception
public class BadRequestException extends ResponseStatusException{

	public BadRequestException(String message) {
		super(HttpStatus.BAD_REQUEST, message);
	}

}
