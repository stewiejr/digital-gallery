package com.digitalgallery.repository;

import com.digitalgallery.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);
  Optional<User> findByUsername(String username);
  boolean existsByEmail(String email);
  boolean existsByUsername(String username);
  java.util.List<User> findByDisplayNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(String displayName,
      String username);
}
