package net.navinkumar.channelmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Channel Management Service Application
 * 
 * This service handles integrations with booking channels (OTAs),
 * channel managers, and distribution platforms.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ChannelManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelManagementApplication.class, args);
    }
}