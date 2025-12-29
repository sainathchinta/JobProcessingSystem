package com.example.JobProcessingSystem.Entity;

import com.example.JobProcessingSystem.DTO.JobStatus;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String jobId;

    private String jobType;

    @Lob
    private String payloadJson;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private int retries;

    @Lob
    private String resultJson;

    private String errorMessage;
}
