package com.digitalgallery.controller;

import com.digitalgallery.dto.UserDto;
import com.digitalgallery.dto.UserUpdateDtos.UpdateUserRequest;
import com.digitalgallery.entity.User;
import com.digitalgallery.repository.UserRepository;
import com.digitalgallery.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;
  private final UserRepository userRepository;

  public UserController(UserService userService, UserRepository userRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getUser(@PathVariable String id) {
    User user = userService.getById(UUID.fromString(id));
    return ResponseEntity.ok(userService.toDto(user));
  }

  @GetMapping("/by-username")
  public ResponseEntity<UserDto> getByUsername(@RequestParam("username") String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));
    return ResponseEntity.ok(userService.toDto(user));
  }

  @GetMapping("/search")
  public ResponseEntity<List<UserDto>> search(@RequestParam("q") String query) {
    List<UserDto> results = userRepository
        .findByDisplayNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(query, query)
        .stream()
        .map(userService::toDto)
        .collect(Collectors.toList());
    return ResponseEntity.ok(results);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserDto> update(@PathVariable String id, @Valid @RequestBody UpdateUserRequest request) {
    UserDto updated = userService.updateUser(UUID.fromString(id), request);
    return ResponseEntity.ok(updated);
  }
}
