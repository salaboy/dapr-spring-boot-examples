package com.example.demo;

import io.dapr.spring.core.client.DaprClientCustomizer;
import io.dapr.testcontainers.Component;
import io.dapr.testcontainers.DaprContainer;
import io.dapr.testcontainers.DaprLogLevel;
import io.dapr.testcontainers.TestcontainersDaprClientCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@TestConfiguration(proxyBeanMethods = false)
public class DaprTestContainersConfig {


  @Bean
  public DaprClientCustomizer daprClientCustomizer(@Value("${dapr.http.port:0000}") String daprHttpPort,
                                                   @Value("${dapr.grpc.port:0000}") String daprGrpcPort,
                                                   @Value("${dapr.http.endpoint:''}") String daprHttpEndpoint,
                                                   @Value("${dapr.grpc.endpoint:''}") String daprGrpcEndpoint
  ){
    return new TestcontainersDaprClientCustomizer(daprHttpPort, daprGrpcPort, daprHttpEndpoint, daprGrpcEndpoint);
  }

   static final String CONNECTION_STRING =
          "host=postgres-repository user=postgres password=password port=5432 connect_timeout=10 database=dapr_db_repository";
   static final Map<String, String> STATE_STORE_PROPERTIES = createStateStoreProperties();

   static final Map<String, String> BINDING_PROPERTIES = Collections.singletonMap("connectionString", CONNECTION_STRING);

   static final Network DAPR_NETWORK = Network.newNetwork();

   static final WaitStrategy DAPR_CONTAINER_WAIT_STRATEGY = Wait.forHttp("/v1.0/healthz")
          .forPort(3500)
          .forStatusCodeMatching(statusCode -> statusCode >= 200 && statusCode <= 399);

  @Container
   static  PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:16-alpine")
          .withNetworkAliases("postgres-repository")
          .withDatabaseName("dapr_db_repository")
          .withUsername("postgres")
          .withPassword("password")
          .withExposedPorts(5432)
          .withNetwork(DAPR_NETWORK);


  @Container
  static DaprContainer dapr = new DaprContainer("daprio/daprd:1.13.2")
          .withAppName("local-dapr-app")
          .withNetwork(DAPR_NETWORK)
          .withComponent(new Component("kvstore", "state.postgresql", "v1", STATE_STORE_PROPERTIES))
          .withComponent(new Component("kvbinding", "bindings.postgresql", "v1", BINDING_PROPERTIES))
          .withDaprLogLevel(DaprLogLevel.DEBUG)
          .withLogConsumer(outputFrame -> System.out.println(outputFrame.getUtf8String()))
          //.withAppPort(8080)
          .withDaprLogLevel(DaprLogLevel.DEBUG)
          //.withAppChannelAddress("host.testcontainers.internal")
          .waitingFor(DAPR_CONTAINER_WAIT_STRATEGY)
          .dependsOn(POSTGRESQL_CONTAINER);

  @DynamicPropertySource
  static void daprProperties(DynamicPropertyRegistry registry) {
    org.testcontainers.Testcontainers.exposeHostPorts(8080);
    dapr.start();
    registry.add("dapr.grpc.port", dapr::getGrpcPort);
    registry.add("dapr.http.port", dapr::getHttpPort);
    registry.add("dapr.grpc.endpoint", dapr::getGrpcEndpoint);
    registry.add("dapr.http.endpoint", dapr::getHttpEndpoint);

  }

  private static Map<String, String> createStateStoreProperties() {
    Map<String, String> result = new HashMap<>();

    result.put("keyPrefix", "name");
    result.put("actorStateStore", String.valueOf(true));
    result.put("connectionString", CONNECTION_STRING);

    return result;
  }
}
