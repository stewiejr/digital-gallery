package com.digitalgallery.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class PaymentDtos {
  public record PaymentDto(
      String id,
      String userId,
      String name,
      String email,
      BigDecimal price,
      List<String> purchaseArtworkIds,
      Instant createdAt
  ) {}

  public record CreatePaymentRequest(
      @NotNull BigDecimal price,
      @NotEmpty List<String> purchaseArtworkIds,
      String paymentMethodId
  ) {}

  public record PaymentMethodDto(
      String id,
      String cardholderName,
      String cardNumber,
      String expiryDate,
      Boolean isDefault,
      Instant createdAt
  ) {}

  public record CreatePaymentMethodRequest(
      @NotEmpty String cardholderName,
      @NotEmpty String cardNumber,
      @NotEmpty String expiryDate,
      @NotEmpty String cvv,
      Boolean isDefault
  ) {}
}
