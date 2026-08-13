package com.note.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Bean("virtualThreadExecutor")
    public ExecutorService virtualThreadExecutor() {
        // Creates a new virtual thread for each task, ideal for I/O-bound operations.
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}