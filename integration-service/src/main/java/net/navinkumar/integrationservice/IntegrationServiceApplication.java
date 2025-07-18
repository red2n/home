package net.navinkumar.integrationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Integration Service Application
 * 
 * This service handles external system integrations including
 * payment gateways, accounting systems, CRM systems, and other third-party APIs.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class IntegrationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationServiceApplication.class, args);
    }
}