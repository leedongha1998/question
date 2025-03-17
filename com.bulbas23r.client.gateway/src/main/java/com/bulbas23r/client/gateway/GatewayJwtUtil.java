package com.bulbas23r.client.gateway;

import common.dto.user.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class GatewayJwtUtil {

  private final String SECRET_KEY;

  private final long EXPIRATION_TIME = 1000 * 60 * 60; // 60분
  private final long REFRESH_TOKEN_TIME = 24 * 60 * 60 * 1000L; //1일
  private Key secretKey;

  public GatewayJwtUtil(@Value("${jwt.key}") String secretKey) {
    SECRET_KEY = secretKey;
  }

  @PostConstruct // 딱 한번만 호출하면 되는 자원에 씀. 또 호출하는 것 방지
  public void init() {
    byte[] bytes = SECRET_KEY.getBytes();
    secretKey = Keys.hmacShaKeyFor(bytes);
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
      return true;
    } catch (SecurityException | MalformedJwtException | SignatureException e) {
      log.error("Invalid JWT Signature, 유효하지 않은 JWT 서명입니다.");
    } catch (ExpiredJwtException e) {
      log.error("Expired JWT Token, 만료된 JWT 토큰입니다.");
    } catch (UnsupportedJwtException e) {
      log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰입니다.");
    } catch (IllegalArgumentException e) {
      log.error("JWT claims is empty, 잘못된 JWT 토큰입니다.");
    } return false;
  }

  public String extractUsername(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();

    return (String) claims.get("username");
  }

  public String extractRole(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();

    return (String) claims.get("role");
  }

  public String generateRefreshToken(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String role = userDetails.getAuthorities().stream()
        .findFirst()  // 단일 권한만 가져옴
        .map(GrantedAuthority::getAuthority)  // 권한 이름 추출
        .orElseThrow(() -> new IllegalArgumentException("사용자에게 권한이 없습니다."));

    return Jwts.builder()
        .setSubject(String.valueOf(userDetails.getId()))
        .claim("userId",userDetails.getId())
        .claim("username", userDetails.getUsername())
        .claim("role", role)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME))
        .signWith(secretKey)
        .compact();
  }

  public boolean isTokenExpired(String accessToken) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(accessToken)
          .getBody();
      Date expirationDate = claims.getExpiration();
      return expirationDate.before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    } catch (Exception e) {
      //토큰이 유효하지 않을 때
      return true;
    }
  }

  public Long extractUserId(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();

    return (Long) claims.get("userId");
  }

  public Claims getUserInfo(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
