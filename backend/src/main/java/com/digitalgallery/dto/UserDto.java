package com.digitalgallery.dto;

import java.time.Instant;

public record UserDto(
    String id,
    String email,
    String displayName,
    String username,
    String profilePicture,
    Instant createdAt
) {}
