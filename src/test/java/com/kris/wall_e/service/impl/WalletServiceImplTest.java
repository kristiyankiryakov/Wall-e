package com.kris.wall_e.service.impl;

import com.kris.wall_e.dto.UserResponseDto;
import com.kris.wall_e.dto.WalletResponse;
import com.kris.wall_e.entity.User;
import com.kris.wall_e.entity.Wallet;
import com.kris.wall_e.exception.AlreadyExistsException;
import com.kris.wall_e.exception.NotFoundException;
import com.kris.wall_e.mapper.UserMapper;
import com.kris.wall_e.repository.WalletRepository;
import com.kris.wall_e.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    void createWalletShouldCreateWallet() {
        User user = User.builder()
                .id(1L)
                .name("pesho peshev")
                .email("pesho@peshev.com")
                .password("hashed_pesho")
                .build();

        UserResponseDto userResponseDto = new UserResponseDto(1L, "pesho", "pesho@test.com");

        when(userService.getUser(1L)).thenReturn(user);
        when(repository.existsByUserId(1L)).thenReturn(false);
        when(userMapper.fromUser(user)).thenReturn(userResponseDto);

        WalletResponse response = walletService.createWallet(1L);

        assertNotNull(response);
        assertEquals(userResponseDto, response.userDetails());
        assertEquals(BigDecimal.ZERO, response.balance());

        verify(repository, times(1)).save(any(Wallet.class));
        verify(userService).getUser(1L);
        verify(repository).existsByUserId(1L);
    }

    @Test
    void createWallet_ThrowsWhenWalletExists() {

        Long userId = 1L;
        User user = User.builder()
                .id(1L)
                .build();

        when(userService.getUser(userId)).thenReturn(user);
        when(repository.existsByUserId(userId)).thenReturn(true);


        assertThrows(AlreadyExistsException.class,
                () -> walletService.createWallet(userId));

        verify(repository, never()).save(any(Wallet.class));
        verify(userService).getUser(userId);
        verify(repository).existsByUserId(userId);
    }

    @Test
    void createWallet_PropagatesUserServiceException() {

        Long userId = 1L;
        when(userService.getUser(userId)).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class,
                () -> walletService.createWallet(userId));

        verify(repository, never()).save(any(Wallet.class));
        verify(userService).getUser(userId);
        verify(repository, never()).existsByUserId(userId);
    }

}