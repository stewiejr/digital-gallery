package com.digitalgallery.repository;

import com.digitalgallery.entity.Exhibition;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExhibitionRepository extends JpaRepository<Exhibition, UUID> {
  List<Exhibition> findByExhibitionNameContainingIgnoreCase(String name);

  @Modifying
  @Query(value = "DELETE FROM exhibition_artworks WHERE artwork_id = :artworkId", nativeQuery = true)
  void deleteArtworkLinks(@Param("artworkId") UUID artworkId);
}
