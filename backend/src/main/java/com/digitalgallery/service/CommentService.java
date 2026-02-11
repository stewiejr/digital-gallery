package com.digitalgallery.service;

import com.digitalgallery.dto.CommentDtos.CommentDto;
import com.digitalgallery.dto.CommentDtos.CreateCommentRequest;
import com.digitalgallery.entity.Artwork;
import com.digitalgallery.entity.ArtworkComment;
import com.digitalgallery.entity.User;
import com.digitalgallery.entity.UserComment;
import com.digitalgallery.repository.ArtworkCommentRepository;
import com.digitalgallery.repository.ArtworkRepository;
import com.digitalgallery.repository.UserCommentRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CommentService {
  private final ArtworkCommentRepository artworkCommentRepository;
  private final UserCommentRepository userCommentRepository;
  private final ArtworkRepository artworkRepository;
  private final UserService userService;

  public CommentService(ArtworkCommentRepository artworkCommentRepository, UserCommentRepository userCommentRepository,
      ArtworkRepository artworkRepository, UserService userService) {
    this.artworkCommentRepository = artworkCommentRepository;
    this.userCommentRepository = userCommentRepository;
    this.artworkRepository = artworkRepository;
    this.userService = userService;
  }

  public List<CommentDto> listArtworkComments(UUID artworkId) {
    Artwork artwork = artworkRepository.findById(artworkId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artwork not found"));

    return artworkCommentRepository.findByArtworkOrderByCreatedAtAsc(artwork).stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  public CommentDto addArtworkComment(UUID artworkId, UUID authorId, CreateCommentRequest request) {
    Artwork artwork = artworkRepository.findById(artworkId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artwork not found"));
    User author = userService.getById(authorId);

    ArtworkComment comment = new ArtworkComment();
    comment.setArtwork(artwork);
    comment.setAuthor(author);
    comment.setAuthorName(author.getDisplayName());
    comment.setAuthorUsername(author.getUsername());
    comment.setText(request.text());

    ArtworkComment saved = artworkCommentRepository.save(comment);
    return toDto(saved);
  }

  public List<CommentDto> listUserComments(UUID userId) {
    User targetUser = userService.getById(userId);
    return userCommentRepository.findByTargetUserOrderByCreatedAtAsc(targetUser).stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  public CommentDto addUserComment(UUID userId, UUID authorId, CreateCommentRequest request) {
    User targetUser = userService.getById(userId);
    User author = userService.getById(authorId);

    UserComment comment = new UserComment();
    comment.setTargetUser(targetUser);
    comment.setAuthor(author);
    comment.setAuthorName(author.getDisplayName());
    comment.setAuthorUsername(author.getUsername());
    comment.setText(request.text());

    UserComment saved = userCommentRepository.save(comment);
    return toDto(saved);
  }

  private CommentDto toDto(ArtworkComment comment) {
    return new CommentDto(
        comment.getId().toString(),
        comment.getAuthor().getId().toString(),
        comment.getAuthorName(),
        comment.getAuthorUsername(),
        comment.getText(),
        comment.getCreatedAt()
    );
  }

  private CommentDto toDto(UserComment comment) {
    return new CommentDto(
        comment.getId().toString(),
        comment.getAuthor().getId().toString(),
        comment.getAuthorName(),
        comment.getAuthorUsername(),
        comment.getText(),
        comment.getCreatedAt()
    );
  }
}
