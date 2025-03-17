package com.bulbas23r.client.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

  @Value("${jwt.key}")
  private String jwtSecret;

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private static final String USER_ID_HEADER = "X-User-Id";
  private static final String USERNAME_HEADER = "X-User-Username";
  private static final String ROLE_HEADER = "X-User-Role";

    private final List<String> openApiEndpoints = List.of(
      "/api/auth/login",
      "/api/auth/refresh-token",
      "/api/auth/logout",
      "/api/users/sign-up",
      "/api/users/test",
      "/actuator");

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();

    // JWT 토큰이 필요 없는 경로 확인 (예: /auth/login, /auth/register 등)
    if (isSecuredPath(request)) {
      List<String> authHeader = request.getHeaders().get(AUTHORIZATION_HEADER);

      // Authorization 헤더가 없거나 Bearer 토큰이 아닌 경우
      if (authHeader == null || authHeader.isEmpty() || !authHeader.get(0).startsWith(BEARER_PREFIX)) {
        return onError(exchange, "Authorization header is missing or invalid", HttpStatus.UNAUTHORIZED);
      }

      // Bearer 접두사 제거
      String token = authHeader.get(0).substring(BEARER_PREFIX.length());

      try {
        // JWT 토큰 검증 및 Claims 추출
        Claims claims = validateToken(token);

        // Claims에서 필요한 정보 추출
        String userId = claims.getSubject();
        String username = claims.get("username", String.class);
        String role = claims.get("role", String.class);

        // 추출한 Claims 정보를 헤더에 추가하여 마이크로서비스로 전달
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
            .header(USER_ID_HEADER, userId)
            .header(USERNAME_HEADER, username)
            .header(ROLE_HEADER, role)
            .build();

        // 수정된 요청으로 교체
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
      } catch (Exception e) {
        return onError(exchange, "Invalid JWT token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
      }
    }

    // JWT 검증이 필요 없는 경로는 그대로 통과
    return chain.filter(exchange);
  }

  private boolean isSecuredPath(ServerHttpRequest request) {
    String path = request.getURI().getPath();
    // JWT 검증이 필요 없는 경로 정의 (예외 처리)
    return !openApiEndpoints.contains(path);
  }

  private Claims validateToken(String token) {
    // JWT 시크릿 키를 이용하여 SecretKey 객체 생성
    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

    // JWT 토큰 파싱 및 Claims 추출
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(status);
    return response.setComplete();
  }

  @Override
  public int getOrder() {
    // 필터 실행 순서, 낮은 값이 먼저 실행됨
    return -1;
  }
}
