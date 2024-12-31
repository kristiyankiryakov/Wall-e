//package com.kris.wall_e.service.impl;
//
//import com.kris.wall_e.dto.UserDto;
//import com.kris.wall_e.entity.User;
//import com.kris.wall_e.exception.NotFoundException;
//import com.kris.wall_e.exception.AlreadyExistsException;
//import com.kris.wall_e.mapper.UserMapper;
//import com.kris.wall_e.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class UserServiceImplTest {
//
//    @Mock
//    private UserRepository repository;
//
//    @Mock
//    private UserMapper mapper;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UserServiceImpl service;
//
//    @Test
//    void createUserShouldCreateUser() {
//
//        UserDto userDto = new UserDto("Pesho Peshev", "pesho@peshev.com", "pesho123");
//        User user = User.builder()
//                .name(userDto.name())
//                .email(userDto.email())
//                .password("hashedPesho")
//                .build();
//
//        when(repository.findByEmail(userDto.email())).thenReturn(null);
//        when(passwordEncoder.encode(userDto.password())).thenReturn("hashedPesho");
//        when(repository.save(any(User.class))).thenReturn(user);
//        when(mapper.fromUser(any(User.class))).thenReturn(new UserResponseDto(1L, "Pesho Peshev", "pesho@peshev.com"));
//
//        UserResponseDto result = service.createUser(userDto);
//
//        assertNotNull(result);
//        assertEquals(user.getName(), result.name());
//        assertEquals(user.getEmail(), result.email());
//
//        verify(repository).save(any(User.class));
//        verify(passwordEncoder).encode(userDto.password());
//    }
//
//    @Test
//    void createUserShouldThrowExceptionWhen_UserAlreadyExists() {
//
//        UserDto userDto = new UserDto("John Doe", "johndoe@example.com", "password");
//        when(repository.findByEmail(userDto.email())).thenReturn(new User());
//
//        assertThrows(AlreadyExistsException.class, () -> service.createUser(userDto));
//    }
//
//    @Test
//    void getUserShouldReturnUserWhen_Found() {
//
//        User user = User.builder().id(1L).name("John Doe").email("johndoe@example.com").build();
//        when(repository.findById(1L)).thenReturn(Optional.of(user));
//
//        User result = service.getUser(1L);
//
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//        assertEquals("John Doe", result.getName());
//    }
//
//    @Test
//    void getUserShouldThrowNotFoundExceptionWhen_UserDoesNotExist() {
//
//        when(repository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () -> service.getUser(1L));
//    }
//}