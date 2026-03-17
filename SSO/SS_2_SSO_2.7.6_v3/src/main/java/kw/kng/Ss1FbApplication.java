package kw.kng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Ss1FbApplication extends SpringBootServletInitializer 
{

	public static void main(String[] args) {
		SpringApplication.run(Ss1FbApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) 
	{
		return builder.sources(Ss1FbApplication.class);
	}
}
