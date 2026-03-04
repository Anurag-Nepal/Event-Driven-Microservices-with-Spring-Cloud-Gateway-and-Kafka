package com.anurag.AsyncJobInjestionProducer.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {
    private String jobId;
    private String type;          // "EMAIL", "PDF", "IMAGE"
    private Map<String, Object> payload;  // flexible key‑value data
    private Long createdAt;
}