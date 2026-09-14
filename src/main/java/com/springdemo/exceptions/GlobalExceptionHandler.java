package com.springdemo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.springdemo.dto.ErrorResponseDTO;
import com.springdemo.exceptions.Exceptions.TaskNotFoundException;
import com.springdemo.exceptions.Exceptions.UserAlreadyExistsException;
import com.springdemo.exceptions.Exceptions.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponseDTO> handleUsernameNotFound(UserNotFoundException ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(404, ex.getMessage()));
	}
	
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExists(UserAlreadyExistsException ex){
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO(409, ex.getMessage()));
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).
				body(new ErrorResponseDTO(400, ex.getBindingResult().getFieldError().getDefaultMessage()));
	}
	
	@ExceptionHandler(TaskNotFoundException.class)
	public ResponseEntity<ErrorResponseDTO> handleTaskNotFound(TaskNotFoundException ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(404, ex.getMessage()));
	}
}
