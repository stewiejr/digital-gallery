package com.digitalgallery.controller;

import com.digitalgallery.dto.CommentDtos.CommentDto;
import com.digitalgallery.dto.CommentDtos.CreateCommentRequest;
import com.digitalgallery.service.CommentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
  private final CommentService commentService;

  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @GetMapping("/artworks/{artworkId}")
  public ResponseEntity<List<CommentDto>> listArtworkComments(@PathVariable String artworkId) {
    return ResponseEntity.ok(commentService.listArtworkComments(UUID.fromString(artworkId)));
  }

  @PostMapping("/artworks/{artworkId}")
  public ResponseEntity<CommentDto> addArtworkComment(@PathVariable String artworkId,
      @AuthenticationPrincipal UserDetails userDetails,
      @Valid @RequestBody CreateCommentRequest request) {
    UUID userId = UUID.fromString(userDetails.getUsername());
    return ResponseEntity.ok(commentService.addArtworkComment(UUID.fromString(artworkId), userId, request));
  }

  @GetMapping("/users/{userId}")
  public ResponseEntity<List<CommentDto>> listUserComments(@PathVariable String userId) {
    return ResponseEntity.ok(commentService.listUserComments(UUID.fromString(userId)));
  }

  @PostMapping("/users/{userId}")
  public ResponseEntity<CommentDto> addUserComment(@PathVariable String userId,
      @AuthenticationPrincipal UserDetails userDetails,
      @Valid @RequestBody CreateCommentRequest request) {
    UUID authorId = UUID.fromString(userDetails.getUsername());
    return ResponseEntity.ok(commentService.addUserComment(UUID.fromString(userId), authorId, request));
  }
}
