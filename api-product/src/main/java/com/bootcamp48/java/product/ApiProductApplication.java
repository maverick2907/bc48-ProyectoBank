package com.bootcamp48.java.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SuppressWarnings("checkstyle:MissingJavadocType")
@SpringBootApplication
@EnableEurekaClient
@EnableReactiveMongoAuditing
@EnableCaching
public class ApiProductApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiProductApplication.class, args);
  }

}
