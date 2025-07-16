package com.example.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NaturalId;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@BatchSize(size = 20)  // To mitigate "N+1", when "jobs" are fetched as "parent" side of different associations
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "email" })
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Email
    @Column(unique = true)
    @NaturalId
    private String email;

    @NotNull
    @Column
    private String password;

    @NotNull
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Fetch(FetchMode.SUBSELECT)  // To avoid "N+1"
    @Setter(AccessLevel.PROTECTED)
    private Set<Role> roles = new HashSet<>();


    // Utility methods that synchronize both ends whenever another side element is added or removed.
    public void addSpecialties(Collection<Role> roles) {
        roles.forEach(this::addRole);
    }
    public void addRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeSpecialties(Collection<Role> roles) {
        roles.forEach(this::removeRole);
    }
    public void removeRole(Role role) {
        roles.remove(role);
        role.getUsers().remove(this);
    }
}
