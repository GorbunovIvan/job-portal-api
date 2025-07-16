package com.example.model;

import com.example.model.user.UserBased;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "applicants")
@BatchSize(size = 20)  // To mitigate "N+1", when "applicants" are fetched for "applications"
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString(callSuper = true)
public class Applicant extends UserBased {

    @Column(name = "resume_link")
    private String resumeLink;

    @Column(name = "bio")
    private String bio;

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.PRIVATE)
    @BatchSize(size = 20)  // To mitigate "N+1"
    @ToString.Exclude
    private List<Application> applications = new ArrayList<>();


    // Utility methods that synchronize both ends of the "Application" association
    public void addApplications(Collection<Application> applications) {
        applications.forEach(this::addApplication);
    }
    public void addApplication(Application application) {
        application.setApplicant(this);
        if (!applications.contains(application)) {
            applications.add(application);
        }
    }

    public void removeApplications(Collection<Application> applications) {
        applications.forEach(this::removeApplication);
    }
    public void removeApplication(Application application) {
        applications.remove(application);
        application.setApplicant(null);
    }
}
