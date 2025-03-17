package com.bulbas23r.client.user.infrastructure.persistence;

import com.bulbas23r.client.user.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User,Long> {

  Optional<User> findByUsername(String username);
}
