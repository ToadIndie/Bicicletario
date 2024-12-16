package com.trabalho.bicicletario;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AppConfig {

    @Bean(name = "restTemplate") @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
