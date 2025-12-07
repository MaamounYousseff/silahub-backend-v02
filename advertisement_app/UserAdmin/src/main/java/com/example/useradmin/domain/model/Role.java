package com.example.useradmin.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Role
{
    @Id
    private UUID id ;
    private String name;
    private String authorities;
    private Boolean isActive;

    @OneToMany(mappedBy = "role")
    Set<UserRoles> userRolesSet;
}
