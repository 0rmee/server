package com.ormee.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OrmeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrmeeApplication.class, args);
	}

}
