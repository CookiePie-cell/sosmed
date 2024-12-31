package com.project.sosmed;

import com.project.sosmed.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class SosmedApplication {

	public static void main(String[] args) {
		SpringApplication.run(SosmedApplication.class, args);
	}

}
