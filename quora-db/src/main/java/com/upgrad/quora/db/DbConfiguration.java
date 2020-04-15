package com.upgrad.quora.db;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.upgrad.quora.db.entity")
@EnableJpaRepositories(basePackages = "com.upgrad.quora.db.dao")
public class DbConfiguration {
}
