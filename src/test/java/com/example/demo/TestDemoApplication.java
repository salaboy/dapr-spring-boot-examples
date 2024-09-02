package com.example.demo;

import io.dapr.spring.core.client.DaprClientCustomizer;
import io.dapr.testcontainers.TestcontainersDaprClientCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@SpringBootApplication
public class TestDemoApplication {

  public static void main(String[] args) {

    SpringApplication
            .from(DemoApplication::main)
            .with(DaprTestContainersConfig.class)
            .run(args);
  }



}
