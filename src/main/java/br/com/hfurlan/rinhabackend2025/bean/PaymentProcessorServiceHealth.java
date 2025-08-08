package br.com.hfurlan.rinhabackend2025.bean;


import lombok.Data;

@Data
public class PaymentProcessorServiceHealth {
    private boolean failing;
    private int minResponseTime;
}
