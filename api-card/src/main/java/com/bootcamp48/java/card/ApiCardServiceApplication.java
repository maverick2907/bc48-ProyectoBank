package com.bootcamp48.java.card;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SuppressWarnings("checkstyle:MissingJavadocType")
@SpringBootApplication
@EnableEurekaClient
@EnableReactiveFeignClients
@EnableReactiveMongoAuditing
public class ApiCardServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiCardServiceApplication.class, args);
  }

}
