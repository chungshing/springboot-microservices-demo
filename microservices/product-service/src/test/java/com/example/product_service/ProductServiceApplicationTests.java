package com.example.product_service;

import com.example.product_service.model.Product;
import com.example.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll(); // Clear the database before each test
	}

	@Test
	void shouldCreateProduct() throws Exception {
		mockMvc.perform(post("/api/product")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Product D\",\"description\":\"Description for Product D\",\"price\":49.99}"))
				.andExpect(status().isCreated());
	}

	@Test
	void shouldGetAllProducts() throws Exception {
		// Ensure there is at least one product in the database
		productRepository.save(new Product(null, "Product E", "Description for Product E", new BigDecimal("59.99")));

		mockMvc.perform(get("/api/product")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Product E"));
	}
}