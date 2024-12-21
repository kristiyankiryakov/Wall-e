package com.kris.wall_e.service.impl;

import com.kris.wall_e.dto.UserDto;
import com.kris.wall_e.dto.UserResponseDto;
import com.kris.wall_e.entity.User;
import com.kris.wall_e.repository.UserRepository;
import com.kris.wall_e.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto createUser(UserDto userDto) {
        User user = new User();

        user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setName(userDto.name());

        repository.save(user);

        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                userDto.name()
        );
    }



}
