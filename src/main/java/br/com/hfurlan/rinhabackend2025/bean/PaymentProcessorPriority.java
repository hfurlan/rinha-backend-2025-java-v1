package br.com.hfurlan.rinhabackend2025.bean;

import java.time.Duration;
import java.time.LocalDateTime;

public record PaymentProcessorPriority (
        int paymentProcessor,
        LocalDateTime updatedAt
) {

    public boolean isExpired(){
        return Duration.between(updatedAt, LocalDateTime.now()).toSeconds() > 5;
    }
}
