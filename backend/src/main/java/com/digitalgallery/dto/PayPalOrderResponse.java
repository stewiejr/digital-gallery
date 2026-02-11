package com.digitalgallery.dto;

public class PayPalOrderResponse {
    private String orderId;
    private String approvalUrl;

    public PayPalOrderResponse() {}

    public PayPalOrderResponse(String orderId, String approvalUrl) {
        this.orderId = orderId;
        this.approvalUrl = approvalUrl;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getApprovalUrl() {
        return approvalUrl;
    }

    public void setApprovalUrl(String approvalUrl) {
        this.approvalUrl = approvalUrl;
    }
}
