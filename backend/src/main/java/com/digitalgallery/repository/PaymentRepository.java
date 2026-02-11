package com.digitalgallery.repository;

import com.digitalgallery.entity.Payment;
import com.digitalgallery.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
  List<Payment> findByUserOrderByCreatedAtDesc(User user);

  @Modifying
  @Query(value = "DELETE FROM payment_items WHERE artwork_id = :artworkId", nativeQuery = true)
  void deleteItemsByArtworkId(@Param("artworkId") UUID artworkId);
}
