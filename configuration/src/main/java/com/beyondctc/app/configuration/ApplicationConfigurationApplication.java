package com.beyondctc.app.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigServer
public class ApplicationConfigurationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationConfigurationApplication.class, args);
	}

}
