package com.digitalgallery.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {
  public record RegisterRequest(
      @NotBlank @Email String email,
      @NotBlank String displayName,
      @NotBlank String username,
      @NotBlank String password
  ) {}

  public record LoginRequest(
      @NotBlank String identifier,
      @NotBlank String password
  ) {}

  public record AuthResponse(
      String token,
      UserDto user
  ) {}
}
