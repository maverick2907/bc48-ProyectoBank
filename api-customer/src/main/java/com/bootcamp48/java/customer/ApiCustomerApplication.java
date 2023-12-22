package com.bootcamp48.java.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SuppressWarnings("checkstyle:MissingJavadocType")
@SpringBootApplication
@EnableEurekaClient
@EnableReactiveMongoAuditing
public class ApiCustomerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiCustomerApplication.class, args);
  }

}
