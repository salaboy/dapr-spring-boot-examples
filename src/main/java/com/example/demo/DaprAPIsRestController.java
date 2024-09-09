package com.example.demo;

import io.dapr.client.DaprClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DaprAPIsRestController {
  @Autowired
  private DaprClient daprClient;

  @PostMapping("/store")
  public void storeOrder(@RequestBody Order order){
    daprClient.saveState("kvstore", order.orderId(), order).block();
  }

  @PostMapping("/pubsub")
  public void pubSubMessage(@RequestBody Order order){
    System.out.println("PRODUCE +++++ " + order);
    daprClient.publishEvent("pubsub", "topic", order).block();
  }

}

record Order(@Id String orderId, Integer amount){}
