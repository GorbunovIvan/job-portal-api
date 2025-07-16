package com.example.model;

import com.example.model.enums.JobType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(
        name = "jobs",
        uniqueConstraints = @UniqueConstraint(columnNames = { "title", "location", "employer_id", "posted_at" }),
        indexes = {
                @Index(name = "idx_jobs_employer_id", columnList = "employer_id")
        }
)
@BatchSize(size = 20)  // To mitigate "N+1", when "jobs" are fetched as "parent" side of different associations
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "title", "location", "employer", "postedAt" })
@ToString
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "title")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "location")
    private String location;

    @Column(name = "salary_min")
    private Integer salaryMin;

    @Column(name = "salary_max")
    private Integer salaryMax;

    @ManyToOne
    @JoinColumn(name = "employer_id")
    @Setter(AccessLevel.PROTECTED)
    private Employer employer;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type")
    private JobType jobType;

    @Column(name = "posted_at")
    private Instant postedAt = Instant.now();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.PRIVATE)
    @BatchSize(size = 20)  // To mitigate "N+1"
    @ToString.Exclude
    private List<Application> applications = new ArrayList<>();


    // Utility methods that synchronize both ends of the "Application" association
    public void addApplications(Collection<Application> applications) {
        applications.forEach(this::addApplication);
    }
    public void addApplication(Application application) {
        application.setJob(this);
        if (!applications.contains(application)) {
            applications.add(application);
        }
    }

    public void removeApplications(Collection<Application> applications) {
        applications.forEach(this::removeApplication);
    }
    public void removeApplication(Application application) {
        applications.remove(application);
        application.setJob(null);
    }
}
