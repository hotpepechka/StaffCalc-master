package com.example.StaffCalc;

import com.example.StaffCalc.config.CalculateProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(value = {CalculateProperties.class})
@SpringBootApplication
public class TestkApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestkApplication.class, args);
	}

}
