package com.digitalgallery.controller;

import com.digitalgallery.dto.PaymentDtos;
import com.digitalgallery.service.PaymentMethodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowedHeaders = "*")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    private String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            // The username is the user ID from our JWT filter
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<PaymentDtos.PaymentMethodDto>> listByUser(
            @PathVariable String userId) {
        // Users can only see their own payment methods
        String currentUserId = getCurrentUserId();
        if (currentUserId == null || !currentUserId.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(paymentMethodService.listByUser(userId));
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<PaymentDtos.PaymentMethodDto> create(
            @PathVariable String userId,
            @Valid @RequestBody PaymentDtos.CreatePaymentMethodRequest requestBody) {
        // Users can only create payment methods for themselves
        String currentUserId = getCurrentUserId();
        if (currentUserId == null || !currentUserId.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(paymentMethodService.create(userId, requestBody));
    }

    @DeleteMapping("/{methodId}")
    public ResponseEntity<?> delete(@PathVariable String methodId) {
        try {
            String currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return ResponseEntity.status(403).build();
            }
            paymentMethodService.delete(currentUserId, methodId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{methodId}")
    public ResponseEntity<PaymentDtos.PaymentMethodDto> getById(@PathVariable String methodId) {
        return ResponseEntity.ok(paymentMethodService.getById(methodId));
    }
}
