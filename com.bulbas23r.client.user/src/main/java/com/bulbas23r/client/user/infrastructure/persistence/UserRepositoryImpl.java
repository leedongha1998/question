package com.bulbas23r.client.user.infrastructure.persistence;

import com.bulbas23r.client.user.domain.model.User;
import com.bulbas23r.client.user.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final JpaUserRepository jpaUserRepository;
//  private final JpaQueryFactory jpaQueryFactory;

  @Override
  public User findByUsernameForAuth(String username) {
    return jpaUserRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("username not found"));
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return jpaUserRepository.findByUsername(username);
  }

  @Override
  public boolean existsByUsername(String username) {
    return jpaUserRepository.findByUsername(username).isPresent();
  }

  @Override
  public void save(User user) {
    jpaUserRepository.save(user);
  }

  @Override
  public Optional<User> findById(Long userId) {
    return jpaUserRepository.findById(userId);
  }
}
