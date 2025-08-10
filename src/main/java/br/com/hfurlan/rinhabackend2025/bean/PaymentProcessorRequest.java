package br.com.hfurlan.rinhabackend2025.bean;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentProcessorRequest {
    private String correlationId;
    private BigDecimal amount;
    private String requestedAt;
}
