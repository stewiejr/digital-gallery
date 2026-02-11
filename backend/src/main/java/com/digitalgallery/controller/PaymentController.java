package com.digitalgallery.controller;

import com.digitalgallery.dto.PaymentDtos.CreatePaymentRequest;
import com.digitalgallery.dto.PaymentDtos.PaymentDto;
import com.digitalgallery.service.PaymentService;
import com.digitalgallery.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, allowedHeaders = "*")
public class PaymentController {
  private final PaymentService paymentService;
  private final JwtService jwtService;

  public PaymentController(PaymentService paymentService, JwtService jwtService) {
    this.paymentService = paymentService;
    this.jwtService = jwtService;
  }

  private String getCurrentUserId(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      return jwtService.extractUserId(token);
    }
    return null;
  }

  @GetMapping("/users/{userId}")
  public ResponseEntity<List<PaymentDto>> listByUser(@PathVariable String userId, HttpServletRequest request) {
    String currentUserId = getCurrentUserId(request);
    if (currentUserId == null || !currentUserId.equals(userId)) {
      return ResponseEntity.status(403).build();
    }
    return ResponseEntity.ok(paymentService.listByUser(UUID.fromString(userId)));
  }

  @GetMapping("/users/{userId}/{paymentId}")
  public ResponseEntity<PaymentDto> getById(@PathVariable String userId, @PathVariable String paymentId, HttpServletRequest request) {
    String currentUserId = getCurrentUserId(request);
    if (currentUserId == null || !currentUserId.equals(userId)) {
      return ResponseEntity.status(403).build();
    }
    return ResponseEntity.ok(paymentService.getById(UUID.fromString(userId), UUID.fromString(paymentId)));
  }

  @PostMapping("/users/{userId}")
  public ResponseEntity<PaymentDto> create(@PathVariable String userId,
      @Valid @RequestBody CreatePaymentRequest request, HttpServletRequest httpRequest) {
    String currentUserId = getCurrentUserId(httpRequest);
    if (currentUserId == null || !currentUserId.equals(userId)) {
      return ResponseEntity.status(403).build();
    }
    return ResponseEntity.ok(paymentService.create(UUID.fromString(userId), request));
  }
}
