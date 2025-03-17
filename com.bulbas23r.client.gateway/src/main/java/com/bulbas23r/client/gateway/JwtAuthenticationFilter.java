package com.bulbas23r.client.gateway;

import com.bulbas23r.client.gateway.JwtAuthenticationFilter.Config;
import common.config.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<Config> {
  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    super(Config.class);
    this.jwtUtil = jwtUtil;
  }

  public static class Config {
    // 필요한 설정을 추가할 수 있음
    private boolean includeDebugHeaders;

    public boolean isIncludeDebugHeaders() {
      return includeDebugHeaders;
    }

    public void setIncludeDebugHeaders(boolean includeDebugHeaders) {
      this.includeDebugHeaders = includeDebugHeaders;
    }
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();

      // 인증 헤더 확인
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

        // 요청 헤더에 사용자 정보 추가
        ServerHttpRequest modifiedRequest = request.mutate()
            .header("X-Auth-Username", username)
            .header("X-Auth-Role", role)
            .build();

        // 디버그 헤더 추가 (설정에 따라)
        if (config.isIncludeDebugHeaders()) {
          modifiedRequest = modifiedRequest.mutate()
              .header("X-Debug-Token-Validated", "true")
              .build();
        }

        return chain.filter(exchange.mutate().request(modifiedRequest).build());

      } catch (Exception e) {
        return onError(exchange, "Token validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
      }
    };
  }

  private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(status);
    response.getHeaders().add("X-Auth-Error", message);
    return response.setComplete();
  }
}
