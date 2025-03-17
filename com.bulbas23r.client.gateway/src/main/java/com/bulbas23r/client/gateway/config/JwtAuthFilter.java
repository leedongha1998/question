//package com.bulbas23r.client.gateway.config;
//
//import common.config.JwtUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Collections;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//@Component
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//  private final JwtUtil jwtUtil;
//
//  public JwtAuthFilter(JwtUtil jwtUtil) {
//    this.jwtUtil = jwtUtil;
//  }
//
//  @Override
//  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//      FilterChain chain) throws ServletException, IOException {
//    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//    if (header == null || !header.startsWith("Bearer ")) {
//      chain.doFilter(request, response);
//      return;
//    }
//
//    String token = header.replace("Bearer ", "");
//    if (!jwtUtil.validateToken(token)) {
//      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
//      return;
//    }
//
//    String username = jwtUtil.extractUsername(token);
//    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(jwtUtil.extractRole(token));
//
//    UsernamePasswordAuthenticationToken authentication =
//        new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(authority));
//
//    SecurityContextHolder.getContext().setAuthentication(authentication);
//    chain.doFilter(request, response);
//  }
//}
