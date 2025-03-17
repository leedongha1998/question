package com.bulbas23r.client.user.domain.repository;

import com.bulbas23r.client.user.domain.model.User;
import java.util.Optional;

public interface UserRepository{

  User findByUsernameForAuth(String username);

  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  void save(User user);

  Optional<User> findById(Long userId);
}
