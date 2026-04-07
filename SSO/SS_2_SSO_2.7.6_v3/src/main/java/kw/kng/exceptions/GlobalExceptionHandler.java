package kw.kng.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler
{
	
	/*
 	ResponseEntityExceptionHandler -> this class has all the necessary methods to handle the validation error exceptions.
 */

//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

																		//Handle specific exception - ResourceNotFoundException

@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorDetails> handleAccountException(ResourceNotFoundException ex,	 WebRequest webRequest)
{
	ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
																			  ex.getMessage(),
																			  webRequest.getDescription(false),
																			  "RESOURCE_NOT_FOUND");
	
	return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
}





//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
																				//Handle Generic Exception

@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorDetails> handleGenericException(Exception ex, WebRequest webRequest)
{
	ErrorDetails err= new ErrorDetails(LocalDateTime.now(),
															  ex.getMessage(),
															  webRequest.getDescription(false),
															  "GENERIC_EXCEPTION_->_INTERNAL_SERVER_ERROR");
			
	return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
}

//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

																			 //Handle Validation Errors

//CTRL + SPACE

@Override
protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
																													HttpHeaders headers,
																													HttpStatus status,
																													WebRequest request) 
{
	Map<String, String> errors = new HashMap<>();
	List<ObjectError> errorList = ex.getBindingResult().getAllErrors();
	
	errorList.forEach((error) ->
	{
		String fileName= ((FieldError) error).getField();
		String message = error.getDefaultMessage();
		errors.put(fileName, message);
	});
	
	return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
}

	
	
	

}
