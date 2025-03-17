package com.bulbas23r.client.auth.application.service;

public interface RedisService {

  void saveRefreshToken(String name, String refreshToken);

  String getRefreshToken(String username);

  void deleteRefreshToken(String username);
}
