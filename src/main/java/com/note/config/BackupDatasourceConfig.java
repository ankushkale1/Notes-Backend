package com.note.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "backupEntityManagerFactory",
        transactionManagerRef = "backupTransactionManager",
        basePackages = {"com.note.backup.repo"}
)
public class BackupDatasourceConfig {

    @Bean(name = "backupDataSourceProperties")
    @ConfigurationProperties("spring.datasource.backup")
    public DataSourceProperties backupDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "backupDataSource")
    public DataSource backupDataSource(@Qualifier("backupDataSourceProperties") DataSourceProperties backupDataSourceProperties) {
        return backupDataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "backupEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean backupEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("backupDataSource") DataSource backupDataSource) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

        return builder
                .dataSource(backupDataSource)
                .packages("com.note.pojo")
                .persistenceUnit("backup")
                .properties(properties)
                .build();
    }

    @Bean(name = "backupTransactionManager")
    public PlatformTransactionManager backupTransactionManager(
            @Qualifier("backupEntityManagerFactory") LocalContainerEntityManagerFactoryBean backupEntityManagerFactory) {
        return new JpaTransactionManager(backupEntityManagerFactory.getObject());
    }
}
