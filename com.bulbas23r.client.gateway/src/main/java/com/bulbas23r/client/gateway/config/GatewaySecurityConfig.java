package com.bulbas23r.client.gateway.config;

import com.bulbas23r.client.gateway.JwtAuthenticationFilter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class GatewaySecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)  // CSRF 비활성화 (필요 시 활성화)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)  // 폼 로그인 비활성화
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)  // HTTP 기본 인증 비활성화
        .authorizeExchange(exchange -> exchange
            .pathMatchers("/api/users/sign-up").permitAll()
            .pathMatchers("/api/users/login").permitAll()
            .pathMatchers("/api/users/{userId}").hasAuthority("MASTER")
            .pathMatchers("/api/auth/**").permitAll()
            .pathMatchers("/actuator/**").permitAll()
            .anyExchange().authenticated()  // 그 외 모든 경로는 인증 필요
        )
        .build();
  }

  @Bean
  public CorsWebFilter corsWebFilter() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedOrigins(Arrays.asList("*"));
    corsConfig.setMaxAge(3600L);
    corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    corsConfig.setAllowedHeaders(Arrays.asList("*"));

    org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource source =
        new org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
  }
}
