package org.yanhuang.learning.springboot21;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBoot21UseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBoot21UseApplication.class, args);
	}
}
