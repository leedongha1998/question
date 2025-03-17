package com.bulbas23r.client.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = {"com.bulbas23r.client.user", "common"})
@EnableFeignClients
@EnableDiscoveryClient
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

}
