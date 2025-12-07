package com.example.useradmin.domain.mapper;

import com.example.useradmin.domain.model.User;
import com.example.useradmin.domain.model.UserRoles;
import com.example.useradmin.logic.dto.UserDto;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserMapper
{

    public static List<UserDto> fromUsers(List<User> users)
    {
        List<UserDto> userDtoList = new ArrayList<>();
        users.forEach(e -> userDtoList.add(fromUser(e)));
        return userDtoList;
    }

    public static UserDto fromUser(User user)
    {
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFirstName() + " " +user.getLastName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .logoUrl(user.getLogoUrl())
                .roleNameList(getRoleNameList(user.getUserRolesSet()))
                .status(getStatus(user.getIsEnabled()))
                .isEnabled(user.getIsEnabled())
                .createdAt(user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .lastLogin(user.getLastLogin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .whatsappNumber((user.getWhatsappNumber() != null) ? user.getWhatsappNumber() : "Empty")
                .phoneNumber((user.getPhoneNumber() != null) ? user.getPhoneNumber() : "Empty")
                .rank((user.getRank() != null) ? user.getRank() : 0)
                .longitude((user.getLongitude() != null) ? user.getLongitude() : -1)
                .latitude((user.getLatitude() != null) ? user.getLatitude() : -1)
                .accountNonLocked(user.getAccountNonLocked())
                .updatedAt(user.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .createdBy(user.getCreatedBy().getUsername())
                .updatedBy(user.getUpdatedBy().getUsername())

                .build();
    }


    private static List<String> getRoleNameList(Set<UserRoles> userRolesSet)
    {
        List<String> roles = new ArrayList<>();
        userRolesSet.forEach(e -> roles.add(e.getRole().getName()));
        return roles;
    }

    private static String getStatus(boolean isEnable)
    {
        return isEnable ? "Enable" : "Disable";
    }
}
