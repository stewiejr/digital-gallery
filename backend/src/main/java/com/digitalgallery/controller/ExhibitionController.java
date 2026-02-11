package com.digitalgallery.controller;

import com.digitalgallery.dto.ExhibitionDtos.CreateExhibitionRequest;
import com.digitalgallery.dto.ExhibitionDtos.ExhibitionDto;
import com.digitalgallery.service.ExhibitionService;
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
@RequestMapping("/api/exhibitions")
public class ExhibitionController {
  private final ExhibitionService exhibitionService;

  public ExhibitionController(ExhibitionService exhibitionService) {
    this.exhibitionService = exhibitionService;
  }

  @GetMapping
  public ResponseEntity<List<ExhibitionDto>> listAll() {
    return ResponseEntity.ok(exhibitionService.listAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ExhibitionDto> getById(@PathVariable String id) {
    return ResponseEntity.ok(exhibitionService.getById(UUID.fromString(id)));
  }

  @PostMapping
  public ResponseEntity<ExhibitionDto> create(@AuthenticationPrincipal UserDetails userDetails,
      @Valid @RequestBody CreateExhibitionRequest request) {
    UUID userId = UUID.fromString(userDetails.getUsername());
    return ResponseEntity.ok(exhibitionService.create(userId, request));
  }
}
