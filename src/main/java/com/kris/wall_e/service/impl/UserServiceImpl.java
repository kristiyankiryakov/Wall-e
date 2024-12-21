package com.kris.wall_e.service.impl;

import com.kris.wall_e.dto.UserDto;
import com.kris.wall_e.dto.UserResponseDto;
import com.kris.wall_e.entity.User;
import com.kris.wall_e.exception.UserAlreadyExistsException;
import com.kris.wall_e.repository.UserRepository;
import com.kris.wall_e.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

//    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
//        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto createUser(UserDto userDto) {

        Optional<User> isExistingUser = Optional.ofNullable(repository.findByEmail(userDto.email()));
        if(isExistingUser.isPresent()){
            throw new UserAlreadyExistsException("User with email '" + userDto.email() + "' already exists.");
        }

        User user = new User();

        user.setEmail(userDto.email());
        user.setPassword("asd");
//        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setName(userDto.name());

        repository.save(user);

        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                userDto.name()
        );
    }



}