package com.example.useradmin.logic;

import com.example.useradmin.web.UserDto;
import com.example.useradmin.web.UserDtoMapper;
import com.example.useradmin.domain.model.User;
import com.example.useradmin.infrastructure.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserAdminService
{
    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    public Page<UserDto> getUserDtos(Pageable pageable)
    {
        Page<User> page= this.userRepositoryImpl.getUsers(pageable);
        List<UserDto> userDtos = UserDtoMapper.fromUsers(page.getContent());

        return new PageImpl<>(userDtos,page.getPageable(),page.getTotalElements());
    }

    public User disableUser(UUID userId)
    {
        return this.userRepositoryImpl.disableUser(userId);
    }

    public User enableUser(UUID userId)
    {
        return this.userRepositoryImpl.enableUser(userId);
    }

}
