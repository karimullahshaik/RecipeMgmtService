package com.abnamro.recipes.exceptions;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

//Custom Error Response Class for Recipes Web Service
@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {
	private final int status;
	private final String message;
	private final LocalDateTime dateTime;
}
