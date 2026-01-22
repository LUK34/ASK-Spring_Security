package kw.kng.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingsController 
{

	
	@GetMapping("/hello")
	public String sayHello() 
	{
		return "Hello World";
		
	}
	
	//SpEL -> Cant use @Value annotation, wont work
	@PreAuthorize("hasRole(@environment.getProperty('security.role.user'))") 
	@GetMapping("/user")
	public String user_end_point() 
	{
		return "Hello from USER.";
		
	}

	//SpEL -> Cant use @Value annotation, wont work
	@PreAuthorize("hasRole(@environment.getProperty('security.role.admin'))") 
	@GetMapping("/admin")
	public String admin_end_point() 
	{
		return "Hello from ADMIN.";
		
	}
	
	
}

/*
--------------------------------------------------------------------------------------------------------------------------
 0. The defualt authentication in spring security is FORM BASED AUTHENTICATION.
--------------------------------------------------------------------------------------------------------------------------
1. Without Spring security
URL: localhost:8080/hello
--------------------------------------------------------------------------------------------------------------------------
2. With Spring security when you execute the above url. The url will redirected to default spring security/
URL: localhost:8080/login

-> to access the application you need to take the password from the console of your sts ide.
e.g:
-> Username: user
-> Passwrod: 6cce3dba-98d8-4f3c-b4cf-2ae5bdd8dfd0
-------------------------------------------------------------------------------------------------------------------------
3. After step 2. If you want to logout. there is a logout page as well that can be accessed.
URL: localhost:8080/logout
--------------------------------------------------------------------------------------------------------------------------
4. ---- IMPORTANT ----
-> If you want to setup a default password. Then in the properties file setup the user password.
e.g:
spring.security.user.password=hello_world
-> After updating the property and try accessing the above url.
URL: localhost:8080/hello
-> You will be redirected to login page.
URL: localhost:8080/login
-> Username: user
-> Passwrod: hello_world
--------------------------------------------------------------------------------------------------------------------------
5. ---- IMPORTANT ----
-> Same as step 4. But we add username also.
e.g:
spring.security.user.name= hello
spring.security.user.password=hello_world
-> After updating the property and try accessing the above url.
URL: localhost:8080/hello
-> You will be redirected to login page.
URL: localhost:8080/login
-> Username: hello
-> Passwrod: hello_world
--------------------------------------------------------------------------------------------------------------------------


 
 
 
 
 

 */
