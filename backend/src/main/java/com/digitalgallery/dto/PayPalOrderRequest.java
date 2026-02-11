package com.digitalgallery.dto;

import java.util.List;

public class PayPalOrderRequest {
    private Double amount;
    private String currency;
    private List<String> artworkIds;
    private String returnUrl;
    private String cancelUrl;

    public PayPalOrderRequest() {}

    public PayPalOrderRequest(Double amount, String currency, List<String> artworkIds, String returnUrl, String cancelUrl) {
        this.amount = amount;
        this.currency = currency;
        this.artworkIds = artworkIds;
        this.returnUrl = returnUrl;
        this.cancelUrl = cancelUrl;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<String> getArtworkIds() {
        return artworkIds;
    }

    public void setArtworkIds(List<String> artworkIds) {
        this.artworkIds = artworkIds;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }
}
