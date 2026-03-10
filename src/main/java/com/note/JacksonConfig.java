package com.note;

import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        // 1. Define constraints
        StreamReadConstraints constraints = StreamReadConstraints.builder()
                .maxStringLength(500_000_000)
                .build();

        // 2. Build the Factory (Public API)
        JsonFactory factory = JsonFactory.builder()
                .streamReadConstraints(constraints)
                .build();

        // 3. Create the Mapper via the specific JsonMapper builder
        return JsonMapper.builder(factory)
                .build();
    }
}