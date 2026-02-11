package com.digitalgallery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "exhibitions")
public class Exhibition {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String exhibitionName;

  @Column(nullable = false, length = 4000)
  private String description;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "creator_id", nullable = false)
  private User creator;

  @Column(nullable = false)
  private String creatorName;

  @Column(nullable = false)
  private String creatorUsername;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "exhibition_artworks",
      joinColumns = @JoinColumn(name = "exhibition_id"),
      inverseJoinColumns = @JoinColumn(name = "artwork_id")
  )
  private Set<Artwork> artworks = new HashSet<>();

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  public void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getExhibitionName() {
    return exhibitionName;
  }

  public void setExhibitionName(String exhibitionName) {
    this.exhibitionName = exhibitionName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public User getCreator() {
    return creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public String getCreatorName() {
    return creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

  public String getCreatorUsername() {
    return creatorUsername;
  }

  public void setCreatorUsername(String creatorUsername) {
    this.creatorUsername = creatorUsername;
  }

  public Set<Artwork> getArtworks() {
    return artworks;
  }

  public void setArtworks(Set<Artwork> artworks) {
    this.artworks = artworks;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
