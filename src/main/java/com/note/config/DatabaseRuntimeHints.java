package com.note.config;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

public class DatabaseRuntimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Register MySQL Driver via string reference
        hints.reflection().registerType(TypeReference.of("com.mysql.cj.jdbc.Driver"));

        // Register H2 Driver via string reference
        hints.reflection().registerType(TypeReference.of("org.h2.Driver"));

        // Register your POJOs if they are used in reflective operations (like JSON serialization)
        hints.reflection().registerType(TypeReference.of("com.note.pojo.Note"));
        hints.reflection().registerType(TypeReference.of("com.note.pojo.Notebook"));

        // Register your custom generator for reflection
        hints.reflection().registerType(com.note.config.SyncIdGenerator.class);

        // Fix for: Invalid logger interface org.hibernate.search.mapper.orm.logging.impl.Log
        hints.proxies().registerJdkProxy(
                TypeReference.of("org.hibernate.search.mapper.orm.logging.impl.Log")
        );

        hints.proxies().registerJdkProxy(
                TypeReference.of("org.hibernate.search.util.common.logging.impl.Log")
        );
    }
}

@Configuration
@ImportRuntimeHints(DatabaseRuntimeHints.class)
class NativeConfig {}