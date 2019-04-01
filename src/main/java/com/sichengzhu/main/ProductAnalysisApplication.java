package com.sichengzhu.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.sichengzhu"})
public class ProductAnalysisApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductAnalysisApplication.class, args);
	}

}