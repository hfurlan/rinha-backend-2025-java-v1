package br.com.hfurlan.rinhabackend2025.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@AllArgsConstructor
public class RestConfig {

    @Bean
    public RestTemplate restTemplate(){
//        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
//        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
//        interceptors.add(new LoggingRequestInterceptor());
//        restTemplate.setInterceptors(interceptors);
//        return restTemplate;
        return new RestTemplate();
    }
}