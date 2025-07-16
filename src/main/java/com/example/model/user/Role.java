package com.example.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.NaturalId;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "name" })
@ToString
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(unique = true)
    @NaturalId
    private String name;

    @ManyToMany(mappedBy = "roles")
    @Setter(AccessLevel.PROTECTED)
    @BatchSize(size = 20)  // To mitigate "N+1"
    @ToString.Exclude
    private Set<User> users = new HashSet<>();
}

