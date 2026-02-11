package com.digitalgallery.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public class CommentDtos {
  public record CommentDto(
      String id,
      String authorId,
      String authorName,
      String authorUsername,
      String text,
      Instant createdAt
  ) {}

  public record CreateCommentRequest(
      @NotBlank String text
  ) {}
}
