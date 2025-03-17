package com.bulbas23r.client.auth.application.service;

import com.bulbas23r.client.auth.client.UserClient;
import common.dto.user.CustomUserDetails;
import common.dto.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserServiceImpl implements CustomUserService{

  private final UserClient userClient;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDetailsDto user = userClient.getUserDetails(username);

    return new CustomUserDetails(
        user.getUserId(),
        user.getUsername(),
        user.getPassword(),
        user.getRole()
    );
  }
}
