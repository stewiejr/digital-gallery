package com.digitalgallery.controller;

import com.digitalgallery.dto.ArtworkDtos.ArtworkDto;
import com.digitalgallery.dto.ArtworkDtos.CreateArtworkRequest;
import com.digitalgallery.security.JwtService;
import com.digitalgallery.service.ArtworkService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {
  private final ArtworkService artworkService;
  private final JwtService jwtService;

  public ArtworkController(ArtworkService artworkService, JwtService jwtService) {
    this.artworkService = artworkService;
    this.jwtService = jwtService;
  }

  private String getCurrentUserId(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      return jwtService.extractUserId(token);
    }
    return null;
  }

  @GetMapping
  public ResponseEntity<List<ArtworkDto>> listAll() {
    return ResponseEntity.ok(artworkService.listAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ArtworkDto> getById(@PathVariable String id) {
    return ResponseEntity.ok(artworkService.getById(UUID.fromString(id)));
  }

  @GetMapping("/by-user/{userId}")
  public ResponseEntity<List<ArtworkDto>> listByUser(@PathVariable String userId) {
    return ResponseEntity.ok(artworkService.listByUser(UUID.fromString(userId)));
  }

  @GetMapping("/search")
  public ResponseEntity<List<ArtworkDto>> search(@RequestParam("q") String query) {
    return ResponseEntity.ok(artworkService.searchByTitle(query));
  }

  @PostMapping
  public ResponseEntity<ArtworkDto> create(HttpServletRequest httpRequest,
      @Valid @RequestBody CreateArtworkRequest request) {
    String currentUserId = getCurrentUserId(httpRequest);
    if (currentUserId == null) {
      return ResponseEntity.status(403).build();
    }
    UUID userId = UUID.fromString(currentUserId);
    return ResponseEntity.ok(artworkService.create(userId, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id, HttpServletRequest httpRequest) {
    String currentUserId = getCurrentUserId(httpRequest);
    if (currentUserId == null) {
      return ResponseEntity.status(403).build();
    }
    artworkService.delete(UUID.fromString(id), UUID.fromString(currentUserId));
    return ResponseEntity.noContent().build();
  }
}
