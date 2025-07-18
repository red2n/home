package net.navinkumar.ratemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RateManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(RateManagementApplication.class, args);
    }
}