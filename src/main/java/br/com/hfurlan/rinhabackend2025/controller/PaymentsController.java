package br.com.hfurlan.rinhabackend2025.controller;

import br.com.hfurlan.rinhabackend2025.bean.*;
import br.com.hfurlan.rinhabackend2025.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentsController {

    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;

    @Value("${rinha.processor-default-url}")
    private String processorDefaultUrl;

    @Value("${rinha.processor-fallback-url}")
    private String processorFallbackUrl;

    private PaymentProcessorPriority paymentProcessorPriority;

    @PostMapping(value = "/payments", consumes = "application/json", produces = "application/json")
    public ResponseEntity<HttpStatus> postPayments(@RequestBody Payment payment){
        if (paymentProcessorPriority == null) {
            paymentProcessorPriority = paymentRepository.findPaymentProcessorPriority();
        }

        OffsetDateTime now = LocalDateTime.now().atOffset(ZoneOffset.UTC);
        PaymentProcessorRequest paymentProcessorRequest = new PaymentProcessorRequest();
        paymentProcessorRequest.setAmount(payment.getAmount());
        paymentProcessorRequest.setCorrelationId(payment.getCorrelationId());
        paymentProcessorRequest.setRequestedAt(LocalDateTime.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        try {
            if (paymentProcessorPriority.paymentProcessor() == 1) {
                try {
                    ResponseEntity<String> responseEntity = restTemplate.postForEntity(processorDefaultUrl + "/payments", paymentProcessorRequest, String.class);
                    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                        log.error("Erro HTTP: response {} no paymentProcessor: {}", responseEntity, paymentProcessorPriority.paymentProcessor());
                        return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
                    }
                } catch (RestClientException e) {
                    ResponseEntity<String> responseEntity = restTemplate.postForEntity(processorFallbackUrl + "/payments", paymentProcessorRequest, String.class);
                    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                        log.error("Erro HTTP: response {} no paymentProcessor: {}", responseEntity, paymentProcessorPriority.paymentProcessor());
                        return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
                    }
                    paymentRepository.updatePaymentProcessorPriority(2);
                }
            } else {
                try {
                    ResponseEntity<String> responseEntity = restTemplate.postForEntity(processorFallbackUrl + "/payments", paymentProcessorRequest, String.class);
                    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                        log.error("Erro HTTP: response {} no paymentProcessor: {}", responseEntity, paymentProcessorPriority.paymentProcessor());
                        return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
                    }
                } catch (RestClientException e) {
                    ResponseEntity<String> responseEntity = restTemplate.postForEntity(processorDefaultUrl + "/payments", paymentProcessorRequest, String.class);
                    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                        log.error("Erro HTTP: response {} no paymentProcessor: {}", responseEntity, paymentProcessorPriority.paymentProcessor());
                        return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
                    }
                    paymentRepository.updatePaymentProcessorPriority(1);
                }
            }
            paymentRepository.insertPayment(payment, 1, now);
        } catch (HttpClientErrorException | DuplicateKeyException e) {
            log.error("Erro HTTP: response {} no paymentProcessor: {}", e, paymentProcessorPriority.paymentProcessor());
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/payments-summary")
    public PaymentSummary getPaymentsSummary(@RequestParam String from, @RequestParam String to) {
        LocalDateTime dtlFrom = LocalDateTime.parse(from, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        LocalDateTime dtlTo = LocalDateTime.parse(to, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return paymentRepository.calculateSummary(dtlFrom, dtlTo);
    }
}