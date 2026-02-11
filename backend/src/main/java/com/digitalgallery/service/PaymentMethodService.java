package com.digitalgallery.service;

import com.digitalgallery.dto.PaymentDtos;
import com.digitalgallery.entity.PaymentMethod;
import com.digitalgallery.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public PaymentDtos.PaymentMethodDto create(String userId, PaymentDtos.CreatePaymentMethodRequest request) {
        // Store only last 4 digits of card
        String last4 = request.cardNumber().length() >= 4 
            ? request.cardNumber().substring(request.cardNumber().length() - 4) 
            : request.cardNumber();

        PaymentMethod method = new PaymentMethod();
        method.setId(UUID.randomUUID().toString());
        method.setUserId(userId);
        method.setCardholderName(request.cardholderName());
        method.setCardNumber(last4);
        method.setExpiryDate(request.expiryDate());
        method.setIsDefault(request.isDefault() != null && request.isDefault());
        method.setCreatedAt(Instant.now());

        // If this is default, unset other defaults
        if (method.getIsDefault()) {
            var existing = paymentMethodRepository.findByUserIdAndIsDefaultTrue(userId);
            if (existing.isPresent()) {
                existing.get().setIsDefault(false);
                paymentMethodRepository.save(existing.get());
            }
        }

        PaymentMethod saved = paymentMethodRepository.save(method);
        return toDto(saved);
    }

    public List<PaymentDtos.PaymentMethodDto> listByUser(String userId) {
        return paymentMethodRepository.findByUserId(userId)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public PaymentDtos.PaymentMethodDto getById(String methodId) {
        PaymentMethod method = paymentMethodRepository.findById(methodId)
            .orElseThrow(() -> new RuntimeException("Payment method not found"));
        return toDto(method);
    }

    public void delete(String userId, String methodId) {
        PaymentMethod method = paymentMethodRepository.findById(methodId)
            .orElseThrow(() -> new RuntimeException("Payment method not found"));
        
        if (!method.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        paymentMethodRepository.delete(method);
    }

    private PaymentDtos.PaymentMethodDto toDto(PaymentMethod method) {
        return new PaymentDtos.PaymentMethodDto(
            method.getId(),
            method.getCardholderName(),
            "****" + method.getCardNumber(),
            method.getExpiryDate(),
            method.getIsDefault(),
            method.getCreatedAt()
        );
    }
}
