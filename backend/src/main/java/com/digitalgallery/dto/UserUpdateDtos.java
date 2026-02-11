package com.digitalgallery.dto;

import jakarta.validation.constraints.Email;

public class UserUpdateDtos {
  public record UpdateUserRequest(
      String displayName,
      String username,
      @Email String email,
      String profilePicture,
      String password
  ) {}
}
