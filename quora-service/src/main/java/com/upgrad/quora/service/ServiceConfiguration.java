package com.upgrad.quora.service;



import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Enabling the component scan and entity scan of classes in the below mentioned "com.upgrad.quora.service" and "com.upgrad.quora.service.entity" packages respectively.
 */
@Configuration
@ComponentScan(basePackages = {"com.upgrad.quora.service","com.upgrad.quora.db"})
public class ServiceConfiguration {
}
