package net.navinkumar.folio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FolioServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FolioServiceApplication.class, args);
    }
}