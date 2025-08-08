package br.com.hfurlan.rinhabackend2025.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentSummaryProcessor {
    private int totalRequests;
    private BigDecimal totalAmount;
}
