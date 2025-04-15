package com.example.ecommercewebservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ECommerceWebServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceWebServiceApplication.class, args);
	}

}
