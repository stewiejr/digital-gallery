package com.digitalgallery.repository;

import com.digitalgallery.entity.Artwork;
import com.digitalgallery.entity.ArtworkComment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkCommentRepository extends JpaRepository<ArtworkComment, UUID> {
  List<ArtworkComment> findByArtworkOrderByCreatedAtAsc(Artwork artwork);

  void deleteByArtwork(Artwork artwork);
}
