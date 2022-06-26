package com.note;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
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

    @Bean
    public RestTemplate createTemplate(){
        return new RestTemplate();
    }
}
