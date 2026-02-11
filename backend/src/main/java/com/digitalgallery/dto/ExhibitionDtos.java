package com.digitalgallery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.List;

public class ExhibitionDtos {
  public record ExhibitionDto(
      String id,
      String exhibitionName,
      String description,
      String creatorId,
      String creatorName,
      String creatorUsername,
      List<String> artworkIds,
      Instant createdAt
  ) {}

  public record CreateExhibitionRequest(
      @NotBlank String exhibitionName,
      @NotBlank String description,
      @NotEmpty List<String> artworkIds
  ) {}
}
