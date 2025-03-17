package com.bulbas23r.client.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder) {
    return builder.routes()
        // 인증 서비스 라우트 - 인증 필터 적용 안 함
        .route("auth-service", r -> r
            .path("/api/auth/**")
            .uri("lb://auth-service"))

        // 사용자 서비스 라우트 - 인증 필터 적용
        .route("user-service", r -> r
            .path("/api/users/**")
            .filters(f -> f.filter(jwtAuthenticationFilter.apply(
                configureJwtFilter(false))))
            .uri("lb://user-service"))

        .build();
  }

  private JwtAuthenticationFilter.Config configureJwtFilter(boolean includeDebugHeaders) {
    JwtAuthenticationFilter.Config config = new JwtAuthenticationFilter.Config();
    config.setIncludeDebugHeaders(includeDebugHeaders);
    return config;
  }
}
