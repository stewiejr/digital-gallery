package com.digitalgallery.service;

import com.digitalgallery.dto.ExhibitionDtos.CreateExhibitionRequest;
import com.digitalgallery.dto.ExhibitionDtos.ExhibitionDto;
import com.digitalgallery.entity.Artwork;
import com.digitalgallery.entity.Exhibition;
import com.digitalgallery.entity.User;
import com.digitalgallery.repository.ArtworkRepository;
import com.digitalgallery.repository.ExhibitionRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExhibitionService {
  private final ExhibitionRepository exhibitionRepository;
  private final ArtworkRepository artworkRepository;
  private final UserService userService;

  public ExhibitionService(ExhibitionRepository exhibitionRepository, ArtworkRepository artworkRepository,
      UserService userService) {
    this.exhibitionRepository = exhibitionRepository;
    this.artworkRepository = artworkRepository;
    this.userService = userService;
  }

  public List<ExhibitionDto> listAll() {
    return exhibitionRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
  }

  public ExhibitionDto getById(UUID id) {
    Exhibition exhibition = exhibitionRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exhibition not found"));
    return toDto(exhibition);
  }

  public ExhibitionDto create(UUID creatorId, CreateExhibitionRequest request) {
    User creator = userService.getById(creatorId);
    List<UUID> artworkIds = request.artworkIds().stream().map(UUID::fromString).toList();
    List<Artwork> artworks = artworkRepository.findAllById(artworkIds);
    if (artworks.size() != artworkIds.size()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more artworks not found");
    }

    Exhibition exhibition = new Exhibition();
    exhibition.setExhibitionName(request.exhibitionName());
    exhibition.setDescription(request.description());
    exhibition.setCreator(creator);
    exhibition.setCreatorName(creator.getDisplayName());
    exhibition.setCreatorUsername(creator.getUsername());
    exhibition.setArtworks(Set.copyOf(artworks));

    Exhibition saved = exhibitionRepository.save(exhibition);
    return toDto(saved);
  }

  public ExhibitionDto toDto(Exhibition exhibition) {
    List<String> artworkIds = exhibition.getArtworks().stream()
        .map(artwork -> artwork.getId().toString())
        .collect(Collectors.toList());

    return new ExhibitionDto(
        exhibition.getId().toString(),
        exhibition.getExhibitionName(),
        exhibition.getDescription(),
        exhibition.getCreator().getId().toString(),
        exhibition.getCreatorName(),
        exhibition.getCreatorUsername(),
        artworkIds,
        exhibition.getCreatedAt()
    );
  }
}
