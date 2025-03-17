package com.bulbas23r.client.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
  private String accessToken;
  private String refreshToken;
}
