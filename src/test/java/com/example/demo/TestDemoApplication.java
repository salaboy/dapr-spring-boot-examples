package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TestDemoApplication {

  public static void main(String[] args) {

    SpringApplication
            .from(DemoApplication::main)
            .with(DaprTestContainersConfig.class)
            .run(args);
  }



}
