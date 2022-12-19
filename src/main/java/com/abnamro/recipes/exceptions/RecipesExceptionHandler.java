package com.abnamro.recipes.exceptions;

import java.time.LocalDateTime;

import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RecipesExceptionHandler extends ResponseEntityExceptionHandler{
	
	//Common method to build error response and return ErrorResponse Instance
	private ResponseEntity<Object> buildErrorResponse(Exception ex, String message, HttpStatus status){
		ErrorResponse errorResponse = new ErrorResponse(status.value(), ex.getMessage(),LocalDateTime.now());
		log.info("ErrorResponse built for error: "+errorResponse.getMessage());
		return ResponseEntity.status(status).body(errorResponse);
	}
	
	//Exception Method to handle Unprocessable Entity Exception
	@Override
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,HttpHeaders headers,HttpStatus status,
			WebRequest request){
		return buildErrorResponse(ex,ex.getMessage(),status);
	}
	
	//Exception Method to handle Resource Not Found Exception
	@ExceptionHandler(NoSuchRecipeFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> handleRecipeNotFoundException(NoSuchRecipeFoundException ex){
		log.info("Handling NoSuchResourceFoundException from ");
		return buildErrorResponse(ex,ErrorMessages.RECIPE_NOT_FOUND_MSG,ex.getStatus());
	}
	
	//Exception method to handle Internal Server Error
	@ExceptionHandler(InvalidDataAccessResourceUsageException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleDataAccessException(InvalidDataAccessResourceUsageException ex){
		log.info("Hanlding InvalidDataAccessResourceUsageException");
		return buildErrorResponse(ex,ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	//Exception method to handle bad request
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleBadRequestException(BadRequestException ex){
		log.info("Handling BadRequestException");
		return buildErrorResponse(ex,ex.getMessage(),ex.getStatus());
	}
	
	//Exception method to handle unauthroized exception
	@ExceptionHandler(UnAuthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<Object> handleUnauthorizedException(UnAuthorizedException ex){
		log.info("Handling Unauthorized Exception");
		return buildErrorResponse(ex,ex.getMessage(),ex.getStatus());
	}
	
	//Exception method to handle Resource creation failure exception
	@ExceptionHandler(RecipeNotCreatedException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleResourceNotCreatedException(RecipeNotCreatedException ex){
		log.info("Handling resource creation failure exception");
		return buildErrorResponse(ex,ex.getMessage(),ex.getStatus());
	}
	
	//Exception method to handle Recourse conflict exception
	@ExceptionHandler(ResourceConflictException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<Object> handleResourceConflictException(ResourceConflictException ex){
		log.info("Handling resource conflict exception");
		return buildErrorResponse(ex,ex.getMessage(),ex.getStatus());
	}
	
	//Exception method to handle all uncaught exceptions
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleAllUncaughtException(Exception ex){
		log.info("Handling uncaught exception: "+ex.getCause());
		return buildErrorResponse(ex,ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
