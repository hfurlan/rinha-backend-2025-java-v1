package br.com.hfurlan.rinhabackend2025.scheduller;

import br.com.hfurlan.rinhabackend2025.bean.PaymentProcessorServiceHealth;
import br.com.hfurlan.rinhabackend2025.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnExpression(
        "${rinha.payment-processor-scheduller-enabled:true}"
)
@RequiredArgsConstructor
public class PaymentProcessorAvailbiltyScheduller {

    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;

    @Value("${rinha.processor-default-url}")
    private String processorDefaultUrl;

    @Value("${rinha.processor-fallback-url}")
    private String processorFallbackUrl;

    @Scheduled(fixedRate = 50000)
    public void checkAvailability() {
        PaymentProcessorServiceHealth paymentProcessorServiceHealth1 = restTemplate.getForObject(processorDefaultUrl + "/payments/service-health", PaymentProcessorServiceHealth.class);
        PaymentProcessorServiceHealth paymentProcessorServiceHealth2 = restTemplate.getForObject(processorFallbackUrl + "/payments/service-health", PaymentProcessorServiceHealth.class);
        if (paymentProcessorServiceHealth1.isFailing()) {
            paymentRepository.updatePaymentProcessorPriority(2);
        } else if (paymentProcessorServiceHealth2.getMinResponseTime() < paymentProcessorServiceHealth1.getMinResponseTime()) {
            paymentRepository.updatePaymentProcessorPriority(2);
        }
        paymentRepository.updatePaymentProcessorPriority(1);
    }
}
