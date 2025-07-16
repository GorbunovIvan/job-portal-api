package com.example.model;

import com.example.model.user.UserBased;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "employers")
@BatchSize(size = 20)  // To mitigate "N+1", when "jobs" are fetched as "parent" side of different associations
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString(callSuper = true)
public class Employer extends UserBased {

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_website")
    private String companyWebsite;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.PRIVATE)
    @BatchSize(size = 20)  // To mitigate "N+1"
    @ToString.Exclude
    private List<Job> jobs = new ArrayList<>();


    // Utility methods that synchronize both ends of the "Job" association
    public void addJobs(Collection<Job> jobs) {
        jobs.forEach(this::addJob);
    }
    public void addJob(Job job) {
        job.setEmployer(this);
        if (!jobs.contains(job)) {
            jobs.add(job);
        }
    }

    public void removeJobs(Collection<Job> jobs) {
        jobs.forEach(this::removeJob);
    }
    public void removeJob(Job job) {
        jobs.remove(job);
        job.setEmployer(null);
    }
}

