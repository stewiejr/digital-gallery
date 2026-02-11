package com.digitalgallery.service;

import com.digitalgallery.dto.ArtworkDtos.ArtworkDto;
import com.digitalgallery.dto.ArtworkDtos.CreateArtworkRequest;
import com.digitalgallery.entity.Artwork;
import com.digitalgallery.entity.User;
import com.digitalgallery.repository.ArtworkCommentRepository;
import com.digitalgallery.repository.ArtworkRepository;
import com.digitalgallery.repository.ExhibitionRepository;
import com.digitalgallery.repository.PaymentRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ArtworkService {
  private final ArtworkRepository artworkRepository;
  private final ArtworkCommentRepository artworkCommentRepository;
  private final ExhibitionRepository exhibitionRepository;
  private final PaymentRepository paymentRepository;
  private final UserService userService;

  public ArtworkService(ArtworkRepository artworkRepository,
      ArtworkCommentRepository artworkCommentRepository,
      ExhibitionRepository exhibitionRepository,
      PaymentRepository paymentRepository,
      UserService userService) {
    this.artworkRepository = artworkRepository;
    this.artworkCommentRepository = artworkCommentRepository;
    this.exhibitionRepository = exhibitionRepository;
    this.paymentRepository = paymentRepository;
    this.userService = userService;
  }

  public List<ArtworkDto> listAll() {
    return artworkRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
  }

  public ArtworkDto getById(UUID id) {
    Artwork artwork = artworkRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artwork not found"));
    return toDto(artwork);
  }

  public List<ArtworkDto> listByUser(UUID userId) {
    User user = userService.getById(userId);
    return artworkRepository.findByArtist(user).stream().map(this::toDto).collect(Collectors.toList());
  }

  public List<ArtworkDto> searchByTitle(String query) {
    return artworkRepository.findByTitleContainingIgnoreCase(query).stream().map(this::toDto).collect(Collectors.toList());
  }

  public ArtworkDto create(UUID userId, CreateArtworkRequest request) {
    User user = userService.getById(userId);

    Artwork artwork = new Artwork();
    artwork.setTitle(request.title());
    artwork.setDescription(request.description());
    artwork.setImageUrl(request.imageUrl());
    artwork.setArtist(user);
    artwork.setArtistName(user.getDisplayName());
    artwork.setArtistUsername(user.getUsername());
    artwork.setPrice(request.price());

    Artwork saved = artworkRepository.save(artwork);
    return toDto(saved);
  }

  public ArtworkDto toDto(Artwork artwork) {
    return new ArtworkDto(
        artwork.getId().toString(),
        artwork.getTitle(),
        artwork.getDescription(),
        artwork.getImageUrl(),
        artwork.getArtist().getId().toString(),
        artwork.getArtistName(),
        artwork.getArtistUsername(),
        artwork.getPrice(),
        artwork.getIsSold(),
        artwork.getCreatedAt()
    );
  }

  public void markAsSold(UUID artworkId) {
    Artwork artwork = artworkRepository.findById(artworkId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artwork not found"));
    artwork.setIsSold(true);
    artworkRepository.save(artwork);
  }

  @Transactional
  public void delete(UUID artworkId, UUID requesterId) {
    Artwork artwork = artworkRepository.findById(artworkId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artwork not found"));

    if (!artwork.getArtist().getId().equals(requesterId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this artwork");
    }

    paymentRepository.deleteItemsByArtworkId(artworkId);
    artworkCommentRepository.deleteByArtwork(artwork);
    exhibitionRepository.deleteArtworkLinks(artworkId);
    artworkRepository.delete(artwork);
  }
}
