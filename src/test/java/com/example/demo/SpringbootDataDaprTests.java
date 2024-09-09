package com.example.demo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;

import static io.restassured.RestAssured.given;


@SpringBootTest(classes= {TestDemoApplication.class, DaprTestContainersConfig.class},
				webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SpringbootDataDaprTests {

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
						.post("/orders")
						.then()
						.statusCode(200);

		given()
						.contentType(ContentType.JSON)
						.body(
										"""
                    {
                        "orderId": "abc-456",
                        "amount": 3
                    }
                    """
						)
						.when()
						.post("/orders")
						.then()
						.statusCode(200);

		given()
						.contentType(ContentType.JSON)
						.when()
						.get("/orders")
						.then()
						.statusCode(200).body("size()", is(2));
	}

}
