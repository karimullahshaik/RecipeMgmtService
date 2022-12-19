package com.abnamro.recipes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
//Class to represent unauthorized access
public class UnAuthorizedException extends ResponseStatusException{
	public UnAuthorizedException(String message) {
		super(HttpStatus.UNAUTHORIZED, message);
	}
}
