package br.com.hfurlan.rinhabackend2025.bean;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class Payment {
    private String correlationId;
    private BigDecimal amount;
}
