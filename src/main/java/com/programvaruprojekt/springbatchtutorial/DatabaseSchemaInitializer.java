package com.programvaruprojekt.springbatchtutorial;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;


@Configuration
public class DatabaseSchemaInitializer {

    private final DataSource dataSource;

    @Autowired
    public DatabaseSchemaInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void initializeSchema() {
        Resource schemaResource = new ClassPathResource("org/springframework/batch/core/schema-h2.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(schemaResource);
        databasePopulator.execute(dataSource);
    }
}


