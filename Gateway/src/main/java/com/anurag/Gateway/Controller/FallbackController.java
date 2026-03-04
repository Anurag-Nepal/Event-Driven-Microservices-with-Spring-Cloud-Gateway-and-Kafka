package com.anurag.Gateway.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/producer")
    public Mono<String> producerFallback() {
        return Mono.just("Producer service is temporarily unavailable. Please try again later.");
    }

    @RequestMapping("/consumer")
    public Mono<String> consumerFallback() {
        return Mono.just("Consumer service is temporarily unavailable.");
    }

        @RequestMapping("/auth")
        public Mono<String> authFallback () {
            return Mono.just("Authentication service is temporarily unavailable.");
        }

    }