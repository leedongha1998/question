package com.bulbas23r.client.auth.client;

import common.dto.user.UserDetailsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "http://localhost:19092")
public interface UserClient {
  @GetMapping("/api/users/login")
  UserDetailsDto getUserDetails(@RequestParam String username);
}
