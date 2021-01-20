package com.bestarmedia.migration.config;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MultipleMongoProperties {

    @Bean(name = "ktvMongoProperties")
    @Primary
    @ConfigurationProperties(prefix = "spring.data.mongodb.third")
    public MongoProperties ktvMongoProperties() {
        System.out.println("-------------------- ktvMongoProperties init ---------------------");
        return new MongoProperties();
    }


    @Bean(name = "vodMongoProperties")
    @Primary
    @ConfigurationProperties(prefix = "spring.data.mongodb.primary")
    public MongoProperties vodMongoProperties() {
        System.out.println("-------------------- statisMongoProperties init ---------------------");
        return new MongoProperties();
    }

    @Bean(name = "songMongoProperties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.secondary")
    public MongoProperties songMongoProperties() {
        System.out.println("-------------------- listMongoProperties init ---------------------");
        return new MongoProperties();
    }

//    @Bean(name = "thirdMongoProperties")
//    @ConfigurationProperties(prefix = "spring.data.mongodb.third")
//    public MongoProperties thirdMongoProperties() {
//        System.out.println("-------------------- thirdMongoProperties init ---------------------");
//        return new MongoProperties();
//    }
//
//    @Bean(name = "fourthMongoProperties")
//    @ConfigurationProperties(prefix = "spring.data.mongodb.fourth")
//    public MongoProperties fourthMongoProperties() {
//        System.out.println("-------------------- fourthMongoProperties init ---------------------");
//        return new MongoProperties();
//    }
}
