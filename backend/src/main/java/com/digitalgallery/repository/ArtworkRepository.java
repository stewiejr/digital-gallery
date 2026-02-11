package com.digitalgallery.repository;

import com.digitalgallery.entity.Artwork;
import com.digitalgallery.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkRepository extends JpaRepository<Artwork, UUID> {
  List<Artwork> findByArtist(User artist);
  List<Artwork> findByTitleContainingIgnoreCase(String title);
}
