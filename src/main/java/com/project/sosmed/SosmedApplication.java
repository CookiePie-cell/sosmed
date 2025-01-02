package com.project.sosmed;

import com.project.sosmed.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableConfigurationProperties(RsaKeyProperties.class)
@EnableJpaRepositories
@SpringBootApplication
public class SosmedApplication {

	public static void main(String[] args) {
		SpringApplication.run(SosmedApplication.class, args);
	}

}
