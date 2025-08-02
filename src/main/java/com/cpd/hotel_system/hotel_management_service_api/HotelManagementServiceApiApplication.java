package com.cpd.hotel_system.hotel_management_service_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HotelManagementServiceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelManagementServiceApiApplication.class, args);
	}

}
