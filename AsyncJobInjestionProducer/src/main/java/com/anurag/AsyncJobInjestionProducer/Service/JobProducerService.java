package com.anurag.AsyncJobInjestionProducer.Service;

import com.anurag.AsyncJobInjestionProducer.DTO.JobRequest;
import org.springframework.kafka.support.SendResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobProducerService {
    private final KafkaTemplate<String, JobRequest> kafkaTemplate;
    private static final String TOPIC = "async-jobs";

    public Mono<String> createEmailJob(JobRequest request) {
        request.setJobId(UUID.randomUUID().toString());
        request.setCreatedAt(System.currentTimeMillis());

        log.info("Sending job to Kafka topic: {} | JobId: {}", TOPIC, request.getJobId());

        return Mono.fromFuture(kafkaTemplate.send(TOPIC, request.getJobId(), request))
                .doOnSuccess(success ->
                        log.info("Message sent successfully! Topic: {}, Partition: {}, Offset: {}",
                                success.getRecordMetadata().topic(),
                                success.getRecordMetadata().partition(),
                                success.getRecordMetadata().offset()))
                .doOnError(error ->
                        log.error("Failed to send message to Kafka", error))
                .map(result -> "Job submitted with id: " + request.getJobId());
    }


    public Mono<String> createInvoiceJob(JobRequest request) {
        request.setJobId(UUID.randomUUID().toString());
        request.setCreatedAt(System.currentTimeMillis());

        log.info("Sending job to Kafka topic: {} | JobId: {}", TOPIC, request.getJobId());

        return Mono.fromFuture(kafkaTemplate.send(TOPIC, request.getJobId(), request))
                .doOnSuccess(success ->
                        log.info("Message sent successfully! Topic: {}, Partition: {}, Offset: {}",
                                success.getRecordMetadata().topic(),
                                success.getRecordMetadata().partition(),
                                success.getRecordMetadata().offset()))
                .doOnError(error ->
                        log.error("Failed to send message to Kafka", error))
                .map(result -> "Job submitted with id: " + request.getJobId());
    }

}
