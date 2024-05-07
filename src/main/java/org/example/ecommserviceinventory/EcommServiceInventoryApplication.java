package org.example.ecommserviceinventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EcommServiceInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommServiceInventoryApplication.class, args);
    }

}
