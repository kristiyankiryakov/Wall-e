package com.kris.wall_e.service.impl;

import com.kris.wall_e.dto.UserDto;
import com.kris.wall_e.dto.UserResponseDto;
import com.kris.wall_e.entity.User;
import com.kris.wall_e.exception.UserAlreadyExistsException;
import com.kris.wall_e.exception.NotFoundException;
import com.kris.wall_e.mapper.UserMapper;
import com.kris.wall_e.repository.UserRepository;
import com.kris.wall_e.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto createUser(UserDto userDto) {

        Optional<User> isExistingUser = Optional.ofNullable(repository.findByEmail(userDto.email()));
        if (isExistingUser.isPresent()) {
            throw new UserAlreadyExistsException("User with email '" + userDto.email() + "' already exists.");
        }

        User user = User.builder()
                .name(userDto.name())
                .email(userDto.email())
                .password(passwordEncoder.encode(userDto.password()))
                .build();

        repository.save(user);

        return mapper.fromUser(user);
    }

    public User getUser(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("user with id: %s does not exists.".formatted(id)));
    }

}
