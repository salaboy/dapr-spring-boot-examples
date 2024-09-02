package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.io.IOException;


@SpringBootTest(classes= {TestDemoApplication.class},
				webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class DemoApplicationTests {

	@Autowired
	private ObjectMapper mapper;

	@Test
	void testEndpoint() throws InterruptedException, IOException {
		OkHttpClient client = new OkHttpClient.Builder().build();
		String url = "http://localhost:8080/store";

		String orderAsString = mapper.writeValueAsString(new Order("abc-123", 4));
		RequestBody body = RequestBody.create(
						MediaType.parse("application/json"), orderAsString);
		Request request = new Request.Builder().url(url).post(body).build();

		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful() && response.body() != null) {

			}else {
				throw new IOException("Unexpected response: " + response.code());
			}
		}

	}

}
