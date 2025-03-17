package com.bulbas23r.client.auth.application.service;

import com.bulbas23r.client.auth.application.dto.JwtAuthenticationResponseDto;
import com.bulbas23r.client.auth.application.dto.LoginRequestDto;
import common.dto.user.CustomUserDetails;
import common.dto.user.UserDetailsDto;

public interface AuthService {


  void logout(String username);


  JwtAuthenticationResponseDto login(LoginRequestDto request);

  JwtAuthenticationResponseDto refreshToken(String refreshToken);

}
