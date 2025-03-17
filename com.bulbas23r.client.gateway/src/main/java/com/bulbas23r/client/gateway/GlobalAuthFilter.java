package com.bulbas23r.client.gateway;

import common.config.JwtUtil;
import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {

  private final GatewayJwtUtil jwtUtil;
  private final List<String> openApiEndpoints = List.of(
      "/api/auth/login",
      "/api/auth/refresh-token",
      "/api/auth/logout",
      "/actuator");

  public GlobalAuthFilter(GatewayJwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getPath().toString();

    // 인증이 필요 없는 API 경로 체크
    if (isOpenApiEndpoint(path)) {
      return chain.filter(exchange);
    }

    // Authorization 헤더 체크
    if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
      return onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
    }

    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
    }

    // 토큰 추출 및 검증
    String token = authHeader.substring(7);

    try {
      if (!jwtUtil.validateToken(token)) {
        return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
      }

      // 토큰에서 정보 추출
      String username = jwtUtil.extractUsername(token);
      String role = jwtUtil.extractRole(token);

      // 사용자 ID 추출 (있다면)
      Long userId = jwtUtil.extractUserId(token);

      // 요청 헤더에 사용자 정보 추가
      ServerHttpRequest modifiedRequest = request.mutate()
          .header("X-Auth-Name", username)
          .header("X-Auth-Role", role)
          .build();

      if (userId != null) {
        modifiedRequest = modifiedRequest.mutate()
            .header("X-Auth-Id", userId.toString())
            .build();
      }

      return chain.filter(exchange.mutate().request(modifiedRequest).build());

    } catch (Exception e) {
      return onError(exchange, "Token validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }

  private boolean isOpenApiEndpoint(String path) {
    return openApiEndpoints.stream().anyMatch(path::startsWith);
  }

  private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(status);
    response.getHeaders().add("X-Auth-Error", message);
    return response.setComplete();
  }

  @Override
  public int getOrder() {
    return -1; // 높은 우선순위로 필터 실행
  }
}
