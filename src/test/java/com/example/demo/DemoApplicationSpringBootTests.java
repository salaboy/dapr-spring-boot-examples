package com.example.demo;

import io.dapr.springboot.DaprAutoConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(classes= {TestDemoApplication.class, DaprTestContainersConfig.class, DaprAutoConfiguration.class},
				webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class DemoApplicationSpringBootTests {

	@Autowired
	private DemoRestController controller;
	@BeforeAll
	public static void setup(){
		org.testcontainers.Testcontainers.exposeHostPorts(8080);
	}

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost:" + 8080;
	}


	@Test
	void testEndpoint() throws InterruptedException, IOException {
		given()
						.contentType(ContentType.JSON)
						.body(
										"""
                    {
                        "orderId": "abc-123",
                        "amount": 1
                    }
                    """
						)
						.when()
						.post("/pubsub")
						.then()
						.statusCode(200);

		assertEquals(1, controller.getAllEvents().size());

	}

}
