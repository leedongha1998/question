package com.bulbas23r.client.auth.presentation.controller;

import com.bulbas23r.client.auth.application.dto.JwtAuthenticationResponseDto;
import com.bulbas23r.client.auth.application.dto.RefreshTokenRequestDto;
import com.bulbas23r.client.auth.application.service.AuthService;
import com.bulbas23r.client.auth.application.dto.LoginRequestDto;
import com.bulbas23r.client.auth.application.dto.LoginResponseDto;
import common.dto.user.UserDetailsDto;
import common.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final JwtUtil jwtUtil;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
    JwtAuthenticationResponseDto response = authService.login(request);
    LoginResponseDto loginResponseDto = new LoginResponseDto(response.getAccessToken(), response.getRefreshToken());

    HttpHeaders headers = new HttpHeaders();
    headers.set("X-User-Id", String.valueOf(response.getUserId()));
    headers.set("X-User-Username", response.getUsername());
    headers.set("X-User-Role", response.getRole());

    return ResponseEntity.ok().headers(headers).body(loginResponseDto);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequest) {
    String accessToken = refreshTokenRequest.getAccessToken();
    String refreshToken = refreshTokenRequest.getRefreshToken();

    //accessToken 만료 검증
    boolean isExpired = jwtUtil.isTokenExpired(accessToken);
    if(isExpired) {
      JwtAuthenticationResponseDto response = authService.refreshToken(refreshToken);
      LoginResponseDto loginResponseDto = new LoginResponseDto(response.getAccessToken().substring(7), response.getRefreshToken());

      HttpHeaders headers = new HttpHeaders();
      headers.set("X-User-Id", String.valueOf(response.getUserId()));
      headers.set("X-User-Username", response.getUsername());
      headers.set("X-User-Role", response.getRole());


      return ResponseEntity.ok().headers(headers).body(loginResponseDto);
    } else {
      return ResponseEntity.ok("Token is still valid");
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(@RequestHeader("X-User-Name") String username) {
    authService.logout(username);
    return ResponseEntity.noContent().build();
  }
}
