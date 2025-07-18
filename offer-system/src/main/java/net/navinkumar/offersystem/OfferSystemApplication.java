package net.navinkumar.offersystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class OfferSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(OfferSystemApplication.class, args);
    }
}