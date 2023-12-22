package com.bootcamp48.java.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SuppressWarnings("checkstyle:MissingJavadocType")
@SpringBootApplication
@EnableConfigServer
public class ApiConfigServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiConfigServerApplication.class, args);
  }

}
