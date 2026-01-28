package com.example.product_service.config;

import com.example.product_service.model.Product;
import com.example.product_service.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            repository.save(new Product(null, "Product A", "Description for Product A", new BigDecimal("19.99")));
            repository.save(new Product(null, "Product B", "Description for Product B", new BigDecimal("29.99")));
            repository.save(new Product(null, "Product C", "Description for Product C", new BigDecimal("39.99")));
        };
    }
}