package com.yys.product_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yys.product_service.dto.ProductRequest;
import com.yys.product_service.model.Product;
import com.yys.product_service.repo.ProductRepository;
import com.yys.product_service.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest   //  uses the real Spring context and the real ProductRepository
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
	@DynamicPropertySource    // add this property at the spring context dynamically while running the test
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
	}
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	ProductService productService;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() throws Exception {
		ProductRequest productRequest= getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);
   		mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString))
				.andExpect(status().isCreated());
	// Assertions.assertTrue(productRepository.findAll().size()==1);
		Assertions.assertEquals(1,productRepository.findAll().size());
	}

	private ProductRequest getProductRequest(){
		return ProductRequest.builder()
				.name("iPhone 13")
				.description("iPhone 13")
				.price(BigDecimal.valueOf(1200))
				.build();
	}

	@Test
	void load_All_Products() throws Exception {
		productRepository.deleteAll();

		Product product = Product.builder()
				.name("iPhone 13")
				.description("Apple Phone")
				.price(BigDecimal.valueOf(1200))
				.build();

		productRepository.save(product);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("iPhone 13"));
	}

}
