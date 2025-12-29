package com.example.JobProcessingSystem.service;

import com.example.JobProcessingSystem.DTO.JobStatus;
import com.example.JobProcessingSystem.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;           
import org.springframework.kafka.support.KafkaHeaders;   
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder; 
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobProcessor {

    private static final int MAX_RETRIES = 3;            
    private static final String RETRY_HEADER = "retry";  

    private final JobRepository repo;
    private final StringRedisTemplate redis;
    private final KafkaTemplate<String, String> kafka;   

    @KafkaListener(topics = "job-queue", groupId = "job-processing")
    public void processJob(String payload,
                           @org.springframework.messaging.handler.annotation.Header(KafkaHeaders.RECEIVED_KEY) String jobId,
                           @Header(name = RETRY_HEADER, required = false) Integer retryCount 
    ) {
        if (retryCount == null) retryCount = 0;          
        log.info("Processing job {} (retry={})", jobId, retryCount); // ✏️ CHANGED

        try {
            repo.findById(jobId).ifPresent(job -> {
                job.setStatus(JobStatus.IN_PROGRESS);
                repo.save(job);
                redis.opsForValue().set(jobId, JobStatus.IN_PROGRESS.name());
            });

            // Simulate processing
            Thread.sleep(3000);

            repo.findById(jobId).ifPresent(job -> {
                job.setStatus(JobStatus.COMPLETED);
                job.setResultJson("{\"message\":\"Job completed successfully\"}");
                repo.save(job);
                redis.opsForValue().set(jobId, JobStatus.COMPLETED.name());
            });

            log.info("Job {} completed", jobId);

        } catch (Exception e) {

            log.error("Job {} failed", jobId, e);

            Integer finalRetryCount = retryCount;
            repo.findById(jobId).ifPresent(job -> {
                job.setStatus(JobStatus.FAILED);
                job.setErrorMessage(e.getMessage());
                job.setRetries(finalRetryCount + 1);
                repo.save(job);
                redis.opsForValue().set(jobId, JobStatus.FAILED.name());
            });

            // 🔹 ---- RETRY LOGIC ADDED BELOW ----
            if (retryCount < MAX_RETRIES) {
                int nextRetry = retryCount + 1;

                log.warn("Retrying job {} (attempt {}/{})", jobId, nextRetry, MAX_RETRIES);

                kafka.send(
                        MessageBuilder
                                .withPayload(payload)
                                .setHeader(KafkaHeaders.TOPIC, "job-queue")
                                .setHeader(KafkaHeaders.KEY, jobId)
                                .setHeader(RETRY_HEADER, nextRetry)
                                .build()
                );

            } else {
                log.error("Job {} moved to DLQ after {} retries", jobId, MAX_RETRIES);

                // 🔹 ---- DLQ PUBLISH ADDED ----
                kafka.send(
                        MessageBuilder
                                .withPayload(payload)
                                .setHeader(KafkaHeaders.TOPIC, "job-dlq")
                                .setHeader(KafkaHeaders.KEY, jobId)
                                .build()
                );
            }
        }
    }
}
