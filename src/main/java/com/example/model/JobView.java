package com.example.model;

import com.example.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "job_views",
        uniqueConstraints = @UniqueConstraint(columnNames = { "job_id", "user_id", "viewed_at" }),
        indexes = {
                @Index(name = "idx_job_views_job_id", columnList = "job_id"),
                @Index(name = "idx_job_views_user_id", columnList = "user_id")
        }
)
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "job", "user", "viewedAt" })
@ToString
public class JobView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // Can be nullable for anonymous

    @Column(name = "viewed_at")
    private Instant viewedAt = Instant.now();
}
