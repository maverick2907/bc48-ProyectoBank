package com.bootcamp48.java.discoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SuppressWarnings("checkstyle:MissingJavadocType")
@SpringBootApplication
@EnableEurekaServer
public class ApiDiscoveryServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiDiscoveryServiceApplication.class, args);
  }

}
