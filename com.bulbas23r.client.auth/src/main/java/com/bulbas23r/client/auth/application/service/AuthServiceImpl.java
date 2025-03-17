package com.bulbas23r.client.auth.application.service;

import com.bulbas23r.client.auth.application.dto.JwtAuthenticationResponseDto;
import com.bulbas23r.client.auth.application.dto.LoginRequestDto;
import com.bulbas23r.client.auth.client.UserClient;
import common.config.JwtUtil;
import common.dto.user.CustomUserDetails;
import common.dto.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserClient userClient;
  private final JwtUtil jwtUtil;
  private final RedisService redisService;

  @Override
  public void logout(String username) {
    redisService.deleteRefreshToken(username);
  }

  @Override
  public JwtAuthenticationResponseDto login(LoginRequestDto request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword()
        )
    );

    // AccessToken, RefreshToken 생성
    String accessToken = jwtUtil.generateToken(authentication);
    String refreshToken = jwtUtil.generateRefreshToken(authentication);
    //사용자 정보
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Long userId = userDetails.getId();  // Long 타입의 ID
    String username = userDetails.getUsername();  // 사용자 이름
    String role = userDetails.getAuthorities().iterator().next().getAuthority();

    // Redis에 RefreshToken 저장
    redisService.saveRefreshToken(authentication.getName(), refreshToken);

    // 응답 DTO 반환
    return new JwtAuthenticationResponseDto(userId, accessToken, refreshToken, username, role);
  }

  @Override
  public JwtAuthenticationResponseDto refreshToken(String refreshToken) {
    //refreshToken 유효성 확인
    if (jwtUtil.validateToken(refreshToken)) {
      String username = jwtUtil.extractUsername(refreshToken);
      //Redis에서 refreshToken 확인
      String storedToken = redisService.getRefreshToken(username).substring(7);
      if (storedToken != null && storedToken.equals(refreshToken)) {
        //new accessToken
        // CustomUserDetailsService를 이용해 사용자 정보 가져오기
        CustomUserDetails userDetail = getUserDetails(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetail, null,
            userDetail.getAuthorities());
        String newAccessToken = jwtUtil.generateToken(authentication);
        //userDetails
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        String userDetailsUsername = userDetails.getUsername();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        //return 새로운 accessToken과 기존 refreshToken
        return new JwtAuthenticationResponseDto(userId, newAccessToken, refreshToken,
            userDetailsUsername, role);
      } else {
        throw new IllegalArgumentException("Invalid refresh Token");
      }
    } else {
      throw new IllegalArgumentException("Refresh token is expired or invalid");
    }
  }


  private CustomUserDetails getUserDetails(String username) {
    UserDetailsDto user = userClient.getUserDetails(username);

    return new CustomUserDetails(
        user.getUserId(),
        user.getUsername(),
        user.getPassword(),
        user.getRole()
    );
  }
}
