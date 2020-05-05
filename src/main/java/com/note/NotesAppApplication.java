package com.note;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableTransactionManagement
public class NotesAppApplication implements WebMvcConfigurer
{

	@Override
	public void addCorsMappings(CorsRegistry registry)
	{
		registry.addMapping("/**");
	}

	public static void main(String[] args)
	{
		SpringApplication.run(NotesAppApplication.class, args);
	}

}
