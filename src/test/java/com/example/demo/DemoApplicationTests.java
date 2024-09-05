package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dapr.testcontainers.DaprContainer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.io.IOException;

import static io.restassured.RestAssured.given;


@SpringBootTest(classes= {TestDemoApplication.class, DaprTestContainersConfig.class},
				webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class DemoApplicationTests {

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
						.post("/store")
						.then()
						.statusCode(200);


	}

}
