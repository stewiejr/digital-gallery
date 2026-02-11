package com.digitalgallery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public class ArtworkDtos {
  public record ArtworkDto(
      String id,
      String title,
      String description,
      String imageUrl,
      String artistId,
      String artistName,
      String artistUsername,
      BigDecimal price,
      Boolean isSold,
      Instant createdAt
  ) {}

  public record CreateArtworkRequest(
      @NotBlank String title,
      @NotBlank String description,
      @NotBlank String imageUrl,
      @NotNull BigDecimal price
  ) {}
}
