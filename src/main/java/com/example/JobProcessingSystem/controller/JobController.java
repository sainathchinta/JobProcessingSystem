package com.example.JobProcessingSystem.controller;

import com.example.JobProcessingSystem.DTO.JobRequest;
import com.example.JobProcessingSystem.Entity.JobEntity;
import com.example.JobProcessingSystem.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService service;

    @PostMapping
    public Object submit(@RequestBody JobRequest req) throws Exception {
        JobEntity job = service.submitJob(req);
        return new Object() {
            public final String jobId = job.getJobId();
            public final String status = job.getStatus().name();
        };
    }

    @GetMapping("/{jobId}")
    public Object status(@PathVariable String jobId) {
        String status = service.getStatus(jobId);
        return new Object() { public final String jobStatus = status; };
    }
}