package com.example.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

@MappedSuperclass
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "user" })
@ToString
public class UserBased {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "id")
    @MapsId  // Tells Hibernate to take the ID from the "User" entity
    @NaturalId
    private User user;
}
