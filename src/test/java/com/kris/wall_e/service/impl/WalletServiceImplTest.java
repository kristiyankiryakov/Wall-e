package com.kris.wall_e.service.impl;

import com.kris.wall_e.dto.TransactionRequest;
import com.kris.wall_e.dto.TransactionResponse;
import com.kris.wall_e.dto.UserResponseDto;
import com.kris.wall_e.dto.WalletResponse;
import com.kris.wall_e.entity.TransactionType;
import com.kris.wall_e.entity.User;
import com.kris.wall_e.entity.Wallet;
import com.kris.wall_e.exception.AlreadyExistsException;
import com.kris.wall_e.exception.InsufficientFundsException;
import com.kris.wall_e.exception.NotFoundException;
import com.kris.wall_e.mapper.UserMapper;
import com.kris.wall_e.repository.WalletRepository;
import com.kris.wall_e.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

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


    @Test
    void getWallet_ShouldReturnWalletResponse_WhenWalletExists() {
        Long userId = 1L;
        User mockUser = User.builder().id(1L).name("pesho").email("pesho@test.com").build();

        UserResponseDto mockUserResponseDto = new UserResponseDto(userId, "pesho", "pesho@test.com");

        Wallet mockWallet = new Wallet();
        mockWallet.setUser(mockUser);
        mockWallet.setBalance(BigDecimal.valueOf(100));


        when(userMapper.fromUser(mockUser)).thenReturn(mockUserResponseDto);
        when(userService.getUser(userId)).thenReturn(mockUser);
        when(repository.findByUserId(userId)).thenReturn(Optional.of(mockWallet));

        WalletResponse result = walletService.getWallet(userId);

        assertNotNull(result);
        assertEquals(mockUserResponseDto, result.userDetails());
        assertEquals(BigDecimal.valueOf(100), result.balance());

        verify(userService, times(1)).getUser(userId);
        verify(userMapper, times(1)).fromUser(mockUser);
        verify(repository, times(1)).findByUserId(userId);
    }

    @Test
    void getWallet_ShouldThrowNotFoundException_WhenWalletDoesNotExist() {

        Long userId = 1L;
        User mockUser = User.builder().id(1L).name("pesho").email("pesho@test.com").build();

        when(userService.getUser(userId)).thenReturn(mockUser);
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            walletService.getWallet(userId);
        });

        assertEquals("Wallet not found for user: " + userId, exception.getMessage());

        verify(userService, times(1)).getUser(userId);
        verify(repository, times(1)).findByUserId(userId);
    }


    @Test
    @Transactional
    void deposit_ShouldReturnTransactionResponse_WhenWalletExists() {

        Long userId = 1L;
        User mockUser = User.builder().id(1L).name("pesho").email("pesho@test.com").build();

        BigDecimal depositAmount = BigDecimal.valueOf(50.0);
        BigDecimal initialBalance = BigDecimal.valueOf(100.0);
        BigDecimal finalBalance = initialBalance.add(depositAmount);

        Wallet mockWallet = new Wallet();
        mockWallet.setId(1L);
        mockWallet.setUser(mockUser);
        mockWallet.setBalance(initialBalance);

        when(repository.findByUserId(userId)).thenReturn(Optional.of(mockWallet));
        //return the exact Wallet object passed to the save method without modifying it.
        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionRequest request = new TransactionRequest(depositAmount);

        TransactionResponse response = walletService.deposit(userId, request);

        assertNotNull(response);
        assertEquals(1L, response.walletId());
        assertEquals(userId, response.userId());
        assertEquals(initialBalance, response.previousBalance());
        assertEquals(finalBalance, response.currentBalance());
        assertEquals(TransactionType.DEPOSIT, response.transactionType());
        assertEquals(depositAmount, response.transactionAmount());

        verify(repository, times(1)).findByUserId(userId);
        verify(repository, times(1)).save(any(Wallet.class));
    }

    @Test
    @Transactional
    void deposit_ShouldThrowNotFoundException_WhenWalletDoesNotExist() {
        Long userId = 1L;
        BigDecimal depositAmount = BigDecimal.valueOf(50.0);

        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        TransactionRequest request = new TransactionRequest(depositAmount);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            walletService.deposit(userId, request);
        });

        assertEquals("Wallet not found for user: " + userId, exception.getMessage());

        verify(repository, times(1)).findByUserId(userId);
        verify(repository, never()).save(any(Wallet.class));
    }

    @Test
    @Transactional
    void withdraw_ShouldReturnTransactionResponse_WhenSufficientFunds() {

        Long userId = 1L;
        User mockUser = User.builder().id(1L).name("pesho").email("pesho@test.com").build();

        BigDecimal withdrawalAmount = BigDecimal.valueOf(50.0);
        BigDecimal initialBalance = BigDecimal.valueOf(100.0);
        BigDecimal finalBalance = initialBalance.subtract(withdrawalAmount);

        Wallet mockWallet = new Wallet();
        mockWallet.setId(1L);
        mockWallet.setUser(mockUser);
        mockWallet.setBalance(initialBalance);

        when(repository.findByUserId(userId)).thenReturn(Optional.of(mockWallet));
        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionRequest request = new TransactionRequest(withdrawalAmount);

        TransactionResponse response = walletService.withdraw(userId, request);

        assertNotNull(response);
        assertEquals(1L, response.walletId());
        assertEquals(userId, response.userId());
        assertEquals(initialBalance, response.previousBalance());
        assertEquals(finalBalance, response.currentBalance());
        assertEquals(TransactionType.WITHDRAWAL, response.transactionType());
        assertEquals(withdrawalAmount, response.transactionAmount());

        verify(repository, times(1)).findByUserId(userId);
        verify(repository, times(1)).save(any(Wallet.class));
    }

    @Test
    @Transactional
    void withdraw_ShouldThrowNotFoundException_WhenWalletDoesNotExist() {
        Long userId = 1L;
        BigDecimal withdrawalAmount = BigDecimal.valueOf(50.0);

        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        TransactionRequest request = new TransactionRequest(withdrawalAmount);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            walletService.withdraw(userId, request);
        });

        assertEquals("Wallet not found for user: " + userId, exception.getMessage());

        verify(repository, times(1)).findByUserId(userId);
        verify(repository, never()).save(any(Wallet.class));
    }

    @Test
    @Transactional
    void withdraw_ShouldThrowInsufficientFundsException_WhenInsufficientFunds() {

        Long userId = 1L;
        User mockUser = User.builder().id(1L).name("pesho").email("pesho@test.com").build();

        BigDecimal withdrawalAmount = BigDecimal.valueOf(150.0);
        BigDecimal initialBalance = BigDecimal.valueOf(100.0);

        Wallet mockWallet = new Wallet();
        mockWallet.setId(1L);
        mockWallet.setUser(mockUser);
        mockWallet.setBalance(initialBalance);

        when(repository.findByUserId(userId)).thenReturn(Optional.of(mockWallet));

        TransactionRequest request = new TransactionRequest(withdrawalAmount);

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> {
            walletService.withdraw(userId, request);
        });

        assertEquals("Insufficient funds. Current balance: 100.0 , Withdrawal amount : 150.0", exception.getMessage());

        verify(repository, times(1)).findByUserId(userId);
        verify(repository, never()).save(any(Wallet.class));
    }

}