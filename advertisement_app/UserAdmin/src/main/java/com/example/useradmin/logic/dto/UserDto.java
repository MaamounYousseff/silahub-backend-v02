package com.example.useradmin.logic.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;
import java.util.UUID;

@Builder
@Getter
@ToString
public class UserDto
{
    private UUID id;
    private String fullName;
    private String firstName;
    private String lastName;
    private String username;
    private String phoneNumber;
    private String whatsappNumber;
    private Short rank;
    private Double latitude;
    private Double longitude;
    private Boolean accountNonLocked;
    private Boolean isEnabled;
    private Short loginAttempts = 0;
    private String updatedAt;
    private String updatedBy;
    private String status;
    private String logoUrl;
    private Collection<String> roleNameList;
    private String createdAt;
    private String lastLogin;
    private String createdBy;

}
