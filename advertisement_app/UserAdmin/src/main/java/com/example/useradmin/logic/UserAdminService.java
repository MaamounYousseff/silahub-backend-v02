package com.example.useradmin.logic;

import com.example.useradmin.web.UserDto;
import com.example.useradmin.web.UserDtoMapper;
import com.example.useradmin.domain.model.User;
import com.example.useradmin.domain.repo.UserRepository;
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
    private UserRepository userRepository;

    public Page<UserDto> getUserDtos(Pageable pageable)
    {
        Page<User> page= this.userRepository.getUsers(pageable);
        List<UserDto> userDtos = UserDtoMapper.fromUsers(page.getContent());

        return new PageImpl<>(userDtos,page.getPageable(),page.getTotalElements());
    }

    public User disableUser(UUID userId)
    {
        return this.userRepository.disableUser(userId);
    }

    public User enableUser(UUID userId)
    {
        return this.userRepository.enableUser(userId);
    }

}
