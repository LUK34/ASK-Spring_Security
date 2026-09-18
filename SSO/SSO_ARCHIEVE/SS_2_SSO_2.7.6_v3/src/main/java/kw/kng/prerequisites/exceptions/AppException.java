package kw.kng.prerequisites.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class AppException extends RuntimeException
{
	private String message;
	
	public AppException(String message)
	{
		super(message);
	}
	
}