package com.digitalgallery.service;

import com.digitalgallery.dto.UserDto;
import com.digitalgallery.dto.UserUpdateDtos.UpdateUserRequest;
import com.digitalgallery.entity.User;
import com.digitalgallery.repository.UserRepository;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User getById(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

  public UserDto toDto(User user) {
    return new UserDto(
        user.getId().toString(),
        user.getEmail(),
        user.getDisplayName(),
        user.getUsername(),
        user.getProfilePicture(),
        user.getCreatedAt()
    );
  }

  public UserDto updateUser(UUID userId, UpdateUserRequest request) {
    User user = getById(userId);

    if (request.username() != null && !request.username().isBlank()
        && !request.username().equals(user.getUsername())) {
      if (userRepository.existsByUsername(request.username())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
      }
      user.setUsername(request.username());
    }

    if (request.email() != null && !request.email().isBlank()
        && !request.email().equals(user.getEmail())) {
      if (userRepository.existsByEmail(request.email())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
      }
      user.setEmail(request.email());
    }

    if (request.displayName() != null && !request.displayName().isBlank()) {
      user.setDisplayName(request.displayName());
    }

    if (request.profilePicture() != null && !request.profilePicture().isBlank()) {
      user.setProfilePicture(request.profilePicture());
    }

    if (request.password() != null && !request.password().isBlank()) {
      user.setPasswordHash(passwordEncoder.encode(request.password()));
    }

    userRepository.save(user);
    return toDto(user);
  }
}
