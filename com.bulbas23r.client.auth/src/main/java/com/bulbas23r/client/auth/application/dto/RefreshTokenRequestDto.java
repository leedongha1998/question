package com.bulbas23r.client.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequestDto {
  private String accessToken;
  private String refreshToken;
}
