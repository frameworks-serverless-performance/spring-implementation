package com.test.api;

import com.test.api.models.PriceRequest;
import com.test.api.models.PriceRequestResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ApiApplicationTests {

	@Autowired
	ApiController controller;

	@Test
	void contextLoads() {
	}

	@Test
	void testEcho() {
		String result = controller.echo("Hello, World!");
		assertEquals("Hello, World!", result);
	}

	@Test
	void testGetPrice() {
		PriceRequest priceRequest = new PriceRequest();
		priceRequest.setItemId("73eebcb6-a5d8-46ff-8a5e-0b9e79be1489");
		priceRequest.setQuantity(2);
		PriceRequestResponse priceRequestResponse = controller.getPrice(priceRequest);
		assertEquals("73eebcb6-a5d8-46ff-8a5e-0b9e79be1489", priceRequestResponse.getItemId());
		assertEquals(2, priceRequestResponse.getQuantity());
		assertEquals(250, priceRequestResponse.getPerItemPrice());
		assertEquals(500, priceRequestResponse.getTotalPricePreTax());
		assertEquals(0.2, priceRequestResponse.getTaxRate(), 0.0001);
		assertEquals(600, priceRequestResponse.getTotalPriceWithTax());
	}

	@Test
	void testCompute() {
		int[] numbers = {9, 58, 79, 99, 33, 67, 68, 48, 26, 42, 11,
				37, 49, 35, 28, 55, 19, 72, 61, 1, 19, 31, 92, 84, 21,
				99, 25, 29, 42, 61, 64, 84, 99, 40, 85, 39, 11, 13, 29, 49,
				95, 29, 30, 21, 12, 52, 98, 51, 18, 76, 5, 54, 16, 28, 83,
				59, 59, 36, 63, 22, 63, 15, 41, 24, 84, 62, 86, 23, 95, 63,
				99, 46, 40, 57, 97, 6, 82, 96, 88, 66, 60, 99, 92, 75, 58,
				32, 15, 32, 72, 61, 52, 50, 61, 81, 65, 46, 40, 71, 32, 71};
		String hash = controller.compute(numbers);
		assertEquals("f79f064b519bfb1197b5c0f2e0c03c54e52fada9b850d681f7dd305f047ea1bb", hash);
	}

	@Test
	void testParse() {
		// generate 100 random strings
		String[] strings = new String[100];
		for (int i = 0; i < 100; i++) {
			strings[i] = UUID.randomUUID().toString();
		}

		// get random index between 0 and 99
		int index = (int) (Math.random() * 99);

		// get the string at the random index
		String searchString = strings[index];

		// test the parse method
		String result = controller.parse(strings, searchString);

		// make sure the returned index is correct
		assertEquals(Integer.toString(index), result);
	}

	@Test
	void testQuery() {
		String result = controller.query("038aca33-a8c0-4b5d-8543-cc50b8f4895c");
		assertEquals("200", result); // round-trip is set up to take 200 queries
	}

}
