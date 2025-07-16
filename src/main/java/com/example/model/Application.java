package com.example.model;

import com.example.model.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "applications",
        uniqueConstraints = @UniqueConstraint(columnNames = { "applicant_id", "job_id" }),
        indexes = {
                @Index(name = "idx_applications_applicant_id", columnList = "applicant_id"),
                @Index(name = "idx_applications_job_id", columnList = "job_id")
        }
)
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "applicant", "job" })
@ToString
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "applicant_id")
    @Setter(AccessLevel.PROTECTED)
    private Applicant applicant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_id")
    @Setter(AccessLevel.PROTECTED)
    private Job job;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    @Column(name = "resume_link")
    private String resumeLink;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "applied_at")
    private Instant appliedAt = Instant.now();
}

