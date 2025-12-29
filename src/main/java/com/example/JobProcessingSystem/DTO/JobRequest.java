package com.example.JobProcessingSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {
    private String jobType;
    private Map<String, Object> payload;
}
