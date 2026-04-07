package kw.kng.errorhandler;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class ValidationErrorHandler 
{
	public void handleErrors(BindingResult result)
	{
		if (result.hasErrors()) 
		{
		    System.out.println("Validation errors occurred:");

		    result.getFieldErrors().forEach(error -> 
		    {
		        System.out.println("Field: " + error.getField());
		        System.out.println("Rejected Value: " + error.getRejectedValue());
		        System.out.println("Error Message: " + error.getDefaultMessage());
		    });

		    result.getGlobalErrors().forEach(error -> 
		    {
		        System.out.println("Object: " + error.getObjectName());
		        System.out.println("Error Message: " + error.getDefaultMessage());
		    });
		}
	}

}
