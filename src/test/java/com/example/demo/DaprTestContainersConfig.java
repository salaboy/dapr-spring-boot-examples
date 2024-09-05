package com.example.demo;

import io.dapr.testcontainers.Component;
import io.dapr.testcontainers.DaprContainer;
import io.dapr.testcontainers.DaprLogLevel;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@TestConfiguration(proxyBeanMethods = false)
public class DaprTestContainersConfig {

   static final String CONNECTION_STRING =
          "host=postgres-repository user=postgres password=password port=5432 connect_timeout=10 database=dapr_db_repository";
   static final Map<String, String> STATE_STORE_PROPERTIES = createStateStoreProperties();

   static final Map<String, String> BINDING_PROPERTIES = Collections.singletonMap("connectionString", CONNECTION_STRING);

   @Bean
   public Network daprNetwork(){
     return Network.newNetwork();
   }

   @Bean
   public  PostgreSQLContainer<?> postgreSQLContainer(Network daprNetwork){
     return new PostgreSQLContainer<>("postgres:16-alpine")
             .withNetworkAliases("postgres-repository")
             .withDatabaseName("dapr_db_repository")
             .withUsername("postgres")
             .withPassword("password")
             .withExposedPorts(5432)
             .withNetwork(daprNetwork);

   }

   @Bean
   @ServiceConnection
   public DaprContainer daprContainer(Network daprNetwork, PostgreSQLContainer<?> postgreSQLContainer){
     final WaitStrategy DAPR_CONTAINER_WAIT_STRATEGY = Wait.forHttp("/v1.0/healthz")
             .forPort(3500)
             .forStatusCodeMatching(statusCode -> statusCode >= 200 && statusCode <= 399);
     return new DaprContainer("daprio/daprd:1.13.2")
             .withAppName("local-dapr-app")
             .withNetwork(daprNetwork)
             .withComponent(new Component("kvstore", "state.postgresql", "v1", STATE_STORE_PROPERTIES))
             .withComponent(new Component("kvbinding", "bindings.postgresql", "v1", BINDING_PROPERTIES))
             .withDaprLogLevel(DaprLogLevel.DEBUG)
             .withLogConsumer(outputFrame -> System.out.println(outputFrame.getUtf8String()))
             //.withAppPort(8080)
             .withDaprLogLevel(DaprLogLevel.DEBUG)
             //.withAppChannelAddress("host.testcontainers.internal")
             .waitingFor(DAPR_CONTAINER_WAIT_STRATEGY)
             .dependsOn(postgreSQLContainer);
   }


  private static Map<String, String> createStateStoreProperties() {
    Map<String, String> result = new HashMap<>();

    result.put("keyPrefix", "name");
    result.put("actorStateStore", String.valueOf(true));
    result.put("connectionString", CONNECTION_STRING);

    return result;
  }
}
