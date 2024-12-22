package com.kris.wall_e.service.impl;

import com.kris.wall_e.dto.UserDto;
import com.kris.wall_e.dto.UserResponseDto;
import com.kris.wall_e.entity.User;
import com.kris.wall_e.mapper.UserMapper;
import com.kris.wall_e.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void createUserShouldCreateUser() {

        UserDto userDto = new UserDto("Pesho Peshev", "pesho@peshev.com", "pesho123");
        User user = User.builder()
                .name(userDto.name())
                .email(userDto.email())
                .password("hashedPesho")
                .build();

        when(repository.findByEmail(userDto.email())).thenReturn(null);
        when(passwordEncoder.encode(userDto.password())).thenReturn("hashedPesho");
        when(repository.save(any(User.class))).thenReturn(user);
        when(mapper.fromUser(any(User.class))).thenReturn(new UserResponseDto(1L, "Pesho Peshev", "pesho@peshev.com"));

        UserResponseDto result = service.createUser(userDto);

        assertNotNull(result);
        assertEquals(user.getName(), result.name());
        assertEquals(user.getEmail(), result.email());

        verify(repository).save(any(User.class));
        verify(passwordEncoder).encode(userDto.password());
    }
}