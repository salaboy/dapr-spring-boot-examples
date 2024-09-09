package com.example.demo;

import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import io.dapr.spring.data.repository.config.EnableDaprRepositories;
import io.dapr.spring.messaging.DaprMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SpringBootMessagingDaprRestController {
  @Autowired
  private DaprMessagingTemplate<Order> messagingTemplate;


  @PostMapping("/orders/events")
  public void pubSubMessage(@RequestBody Order order){
    System.out.println("PRODUCE +++++ " + order);
    messagingTemplate.send("topic", order);
  }

}

