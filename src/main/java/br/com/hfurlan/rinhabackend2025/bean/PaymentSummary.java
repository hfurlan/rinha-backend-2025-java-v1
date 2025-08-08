package br.com.hfurlan.rinhabackend2025.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentSummary {
    @JsonProperty("default")
    private PaymentSummaryProcessor main;
    private PaymentSummaryProcessor fallback;
}
