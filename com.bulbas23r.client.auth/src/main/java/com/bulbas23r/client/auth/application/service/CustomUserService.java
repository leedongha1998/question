package com.bulbas23r.client.auth.application.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserService {

  UserDetails loadUserByUsername(String username);
}
