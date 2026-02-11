package com.digitalgallery.service;

import com.digitalgallery.dto.PaymentDtos.CreatePaymentRequest;
import com.digitalgallery.dto.PaymentDtos.PaymentDto;
import com.digitalgallery.entity.Payment;
import com.digitalgallery.entity.User;
import com.digitalgallery.repository.PaymentRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentService {
  private final PaymentRepository paymentRepository;
  private final UserService userService;
  private final ArtworkService artworkService;

  public PaymentService(PaymentRepository paymentRepository, UserService userService, ArtworkService artworkService) {
    this.paymentRepository = paymentRepository;
    this.userService = userService;
    this.artworkService = artworkService;
  }

  @Transactional(readOnly = true)
  public List<PaymentDto> listByUser(UUID userId) {
    User user = userService.getById(userId);
    return paymentRepository.findByUserOrderByCreatedAtDesc(user).stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public PaymentDto getById(UUID userId, UUID paymentId) {
    User user = userService.getById(userId);
    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    if (!payment.getUser().getId().equals(user.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Payment does not belong to user");
    }
    return toDto(payment);
  }

  @Transactional
  public PaymentDto create(UUID userId, CreatePaymentRequest request) {
    User user = userService.getById(userId);

    // Validate payment data
    if (request.purchaseArtworkIds() == null || request.purchaseArtworkIds().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No artwork items in payment");
    }

    if (request.price() == null || request.price().compareTo(java.math.BigDecimal.ZERO) <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment amount");
    }

    // In a real system, you would validate the payment method here
    // For now, we simulate payment processing by accepting the payment
    // In production, integrate with PayPal, Stripe, or other payment processors

    Payment payment = new Payment();
    payment.setUser(user);
    payment.setName(user.getDisplayName());
    payment.setEmail(user.getEmail());
    payment.setPrice(request.price());
    List<UUID> artworkIds = request.purchaseArtworkIds().stream().map(UUID::fromString).toList();
    payment.setPurchaseArtworkIds(artworkIds);

    Payment saved = paymentRepository.save(payment);

    // Mark all purchased artworks as sold
    for (UUID artworkId : artworkIds) {
      artworkService.markAsSold(artworkId);
    }

    return toDto(saved);
  }

  private PaymentDto toDto(Payment payment) {
    List<String> artworkIds = payment.getPurchaseArtworkIds().stream()
        .map(UUID::toString)
        .collect(Collectors.toList());

    return new PaymentDto(
        payment.getId().toString(),
        payment.getUser().getId().toString(),
        payment.getName(),
        payment.getEmail(),
        payment.getPrice(),
        artworkIds,
        payment.getCreatedAt()
    );
  }
}
