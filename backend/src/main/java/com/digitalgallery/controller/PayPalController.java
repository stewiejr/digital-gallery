package com.digitalgallery.controller;

import com.digitalgallery.dto.PayPalOrderRequest;
import com.digitalgallery.dto.PayPalOrderResponse;
import com.digitalgallery.service.PayPalService;
import com.digitalgallery.service.PaymentService;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Order;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/paypal")
public class PayPalController {

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PayPalOrderRequest request) {
        try {
            String orderId = payPalService.createOrder(
                request.getAmount(),
                request.getCurrency() != null ? request.getCurrency() : "USD",
                request.getReturnUrl(),
                request.getCancelUrl()
            );

            // Get order details to extract approval URL
            Order order = payPalService.getOrder(orderId);
            String approvalUrl = null;
            
            for (LinkDescription link : order.links()) {
                if ("approve".equals(link.rel())) {
                    approvalUrl = link.href();
                    break;
                }
            }

            PayPalOrderResponse response = new PayPalOrderResponse(orderId, approvalUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create PayPal order: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/capture-order/{orderId}")
    public ResponseEntity<?> captureOrder(
            @PathVariable String orderId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> requestBody) {
        try {
            Order order = payPalService.captureOrder(orderId);
            
            if ("COMPLETED".equals(order.status())) {
                // Create payment record in database
                UUID userId = UUID.fromString(userDetails.getUsername());
                
                // Extract artwork IDs from request body
                @SuppressWarnings("unchecked")
                java.util.List<String> artworkIds = (java.util.List<String>) requestBody.get("artworkIds");
                
                // Get the price from the request
                Object priceObj = requestBody.get("price");
                java.math.BigDecimal price = priceObj instanceof String 
                    ? new java.math.BigDecimal((String) priceObj)
                    : new java.math.BigDecimal(priceObj.toString());
                
                com.digitalgallery.dto.PaymentDtos.CreatePaymentRequest paymentRequest = 
                    new com.digitalgallery.dto.PaymentDtos.CreatePaymentRequest(price, artworkIds, "PayPal");
                
                com.digitalgallery.dto.PaymentDtos.PaymentDto payment = 
                    paymentService.create(userId, paymentRequest);
                
                return ResponseEntity.ok(payment);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Payment not completed");
                return ResponseEntity.status(400).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to capture PayPal order: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable String orderId) {
        try {
            Order order = payPalService.getOrder(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get PayPal order: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
