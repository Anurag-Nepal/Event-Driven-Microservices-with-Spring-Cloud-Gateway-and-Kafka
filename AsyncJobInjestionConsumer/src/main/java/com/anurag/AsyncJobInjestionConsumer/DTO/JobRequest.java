package com.anurag.AsyncJobInjestionConsumer.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {
    private String jobId;
    private String type;
    private Map<String, Object> payload;
    private Long createdAt;
}