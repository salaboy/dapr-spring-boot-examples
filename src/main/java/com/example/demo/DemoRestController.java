package com.example.demo;

import io.dapr.Topic;
import io.dapr.client.DaprClient;
import io.dapr.client.domain.CloudEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoRestController {
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

  @PostMapping("/subscribe")
  @Topic(pubsubName = "pubsub", name = "topic")
  public void subscribe(@RequestBody CloudEvent<Order> cloudEvent){
    System.out.println("CONSUME +++++ " + cloudEvent);
    System.out.println("ORDER +++++ " + cloudEvent.getData());
  }

}

record Order(String orderId, Integer amount){}
