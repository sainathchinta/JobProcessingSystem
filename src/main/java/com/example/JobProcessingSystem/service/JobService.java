package com.example.JobProcessingSystem.service;

import com.example.JobProcessingSystem.DTO.JobRequest;
import com.example.JobProcessingSystem.DTO.JobStatus;
import com.example.JobProcessingSystem.Entity.JobEntity;
import com.example.JobProcessingSystem.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository repo;
    private final KafkaTemplate<String, String> kafka;
    private final StringRedisTemplate redis;
    private final ObjectMapper mapper = new ObjectMapper();

    public JobEntity submitJob(JobRequest req) throws Exception {
        JobEntity job = JobEntity.builder()
                .jobType(req.getJobType())
                .payloadJson(mapper.writeValueAsString(req.getPayload()))
                .status(JobStatus.QUEUED)
                .retries(0)
                .build();

        repo.save(job);

        kafka.send("job-queue", job.getJobId(), job.getPayloadJson());
        redis.opsForValue().set(job.getJobId(), "QUEUED");

        return job;
    }

    public String getStatus(String jobId) {
        String cached = redis.opsForValue().get(jobId);
        if (cached != null) return cached;

        return repo.findById(jobId).map(j -> j.getStatus().name()).orElse("NOT_FOUND");
    }
}