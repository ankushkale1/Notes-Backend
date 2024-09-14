package com.note;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Set the maximum string length allowed for reading (adjust the value as needed)
        StreamReadConstraints constraints = StreamReadConstraints.builder()
                .maxStringLength(500_000_000)  // Set your desired max string length
                .build();

        objectMapper.getFactory().setStreamReadConstraints(constraints);
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }
}