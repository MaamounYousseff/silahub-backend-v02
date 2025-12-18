package com.example.useradmin.domain.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String username;

    private String firstName;
    private String lastName;
    private String logoUrl;
    private String phoneNumber;
    private String whatsappNumber;

    private Short rank;

    private Double latitude;
    private Double longitude;

    private Boolean accountNonLocked;
    private Boolean isEnabled;

    private LocalDateTime lastLogin;

    @Builder.Default
    private Short loginAttempts = 0;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    Set<UserRoles> userRolesSet;


    public static boolean exist(Optional<User> userOpt)
    {
        if (userOpt.isEmpty())
            return false;
        return true;
    }
}
