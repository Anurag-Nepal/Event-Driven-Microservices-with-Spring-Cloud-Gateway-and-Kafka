package com.anurag.AsyncJobInjestionProducer.Controller;

import com.anurag.AsyncJobInjestionProducer.DTO.JobRequest;
import com.anurag.AsyncJobInjestionProducer.Service.JobProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobProducerService producerService;

    @PostMapping("/email")
    public Mono<String> submitEmailJob(@RequestBody JobRequest request) {
        return producerService.createEmailJob(request);
    }

    @PostMapping("/file")
    public Mono<String> submitInvoiceJob(@RequestBody JobRequest request) {
        return producerService.createInvoiceJob(request);
    }
}
