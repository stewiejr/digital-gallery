package com.digitalgallery.service;

import com.digitalgallery.dto.AuthDtos.AuthResponse;
import com.digitalgallery.dto.AuthDtos.LoginRequest;
import com.digitalgallery.dto.AuthDtos.RegisterRequest;
import com.digitalgallery.dto.UserDto;
import com.digitalgallery.entity.User;
import com.digitalgallery.repository.UserRepository;
import com.digitalgallery.security.JwtService;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final UserService userService;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
      UserService userService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.userService = userService;
  }

  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }
    if (userRepository.existsByUsername(request.username())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
    }

    User user = new User();
    user.setEmail(request.email());
    user.setDisplayName(request.displayName());
    user.setUsername(request.username());
    user.setPasswordHash(passwordEncoder.encode(request.password()));

    User saved = userRepository.save(user);
    String token = jwtService.generateToken(saved);
    UserDto dto = userService.toDto(saved);
    return new AuthResponse(token, dto);
  }

  public AuthResponse login(LoginRequest request) {
    Optional<User> userOpt;
    if (request.identifier().contains("@")) {
      userOpt = userRepository.findByEmail(request.identifier());
    } else {
      userOpt = userRepository.findByUsername(request.identifier());
    }

    User user = userOpt.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    String token = jwtService.generateToken(user);
    UserDto dto = userService.toDto(user);
    return new AuthResponse(token, dto);
  }
}
