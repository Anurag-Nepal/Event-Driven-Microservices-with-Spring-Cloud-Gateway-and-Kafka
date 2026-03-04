package com.anurag.AsyncJobInjestionConsumer.Utils;

import com.anurag.AsyncJobInjestionConsumer.DTO.JobRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "async-jobs", groupId = "async-job-group")
    public void processJob(JobRequest job) {
        try {
            // Log the received job in a detailed JSON format
            String jobJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(job);
            log.info("Received job: \n{}", jobJson);

            // Simulate processing by sleeping for a few seconds
            log.info("Processing job type: {} with jobId: {}", job.getType(), job.getJobId());
            Thread.sleep(5000); // 5 seconds delay

            log.info("Finished processing job: {}", job.getJobId());
        } catch (Exception e) {
            log.error("Error processing job: {}", job.getJobId(), e);
        }
    }
}