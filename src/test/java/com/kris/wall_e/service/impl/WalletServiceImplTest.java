package com.kris.wall_e.service.impl;

import com.kris.wall_e.dto.TransactionRequest;
import com.kris.wall_e.dto.TransactionResponse;
import com.kris.wall_e.dto.WalletRequest;
import com.kris.wall_e.dto.WalletResponse;
import com.kris.wall_e.entity.User;
import com.kris.wall_e.entity.Wallet;
import com.kris.wall_e.exception.AlreadyExistsException;
import com.kris.wall_e.exception.InsufficientFundsException;
import com.kris.wall_e.exception.ResourceNotFoundException;
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
    private WalletRepository walletRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserIdentityService userIdentityService;

    @InjectMocks
    private WalletServiceImpl walletService;

    private static final String USERNAME = "testUser";
    private static final Long WALLET_ID = 1L;
    private static final String WALLET_NAME = "Primary Wallet";
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(1000);

    private User createTestUser() {
        User owner = new User();
        owner.setUsername(USERNAME);
        return owner;
    }

    private Wallet createTestWallet() {
        User owner = createTestUser();
        Wallet wallet = new Wallet();
        wallet.setId(WALLET_ID);
        wallet.setName(WALLET_NAME);
        wallet.setOwner(owner);
        wallet.setBalance(INITIAL_BALANCE);
        return wallet;
    }

    @Test
    void createWallet_Success() {

        User owner = new User();
        owner.setUsername(USERNAME);

        WalletRequest request = new WalletRequest(WALLET_NAME);

        when(userIdentityService.getAuthenticatedUsername()).thenReturn(USERNAME);
        when(userService.getUserByUsername(USERNAME)).thenReturn(owner);
        when(walletRepository.existsByNameAndOwnerUsername(WALLET_NAME, USERNAME))
                .thenReturn(false);

        when(walletRepository.save(any(Wallet.class))).thenReturn(new Wallet(1L, BigDecimal.ZERO, WALLET_NAME, owner));

        WalletResponse response = walletService.createWallet(request);

        assertNotNull(response);
        assertEquals(WALLET_NAME, response.name());
        assertEquals(USERNAME, response.owner());
        assertEquals(BigDecimal.ZERO, response.balance());
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void createWallet_Throws_AlreadyExists() {

        User owner = new User();
        owner.setUsername(USERNAME);

        WalletRequest request = new WalletRequest(WALLET_NAME);

        when(userIdentityService.getAuthenticatedUsername()).thenReturn(USERNAME);
        when(userService.getUserByUsername(USERNAME)).thenReturn(owner);
        when(walletRepository.existsByNameAndOwnerUsername(WALLET_NAME, USERNAME))
                .thenReturn(true);

        AlreadyExistsException exception = assertThrows(
                AlreadyExistsException.class,
                () -> walletService.createWallet(request)
        );

        assertEquals(
                "Wallet with name %s already exists.".formatted(WALLET_NAME),
                exception.getMessage()
        );

        verify(userIdentityService).getAuthenticatedUsername();
        verify(userService).getUserByUsername(USERNAME);
        verify(walletRepository).existsByNameAndOwnerUsername(WALLET_NAME, USERNAME);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    @Transactional(readOnly = true)
    void viewBalance_Success() {

        Wallet wallet = createTestWallet();

        when(userIdentityService.getAuthenticatedUsername()).thenReturn(USERNAME);
        when(walletRepository.findByIdAndOwnerUsername(WALLET_ID, USERNAME))
                .thenReturn(Optional.of(wallet));

        WalletResponse response = walletService.viewBalance(WALLET_ID);

        assertNotNull(response);
        assertEquals(WALLET_ID, response.id());
        assertEquals(WALLET_NAME, response.name());
        assertEquals(USERNAME, response.owner());
        assertEquals(INITIAL_BALANCE, response.balance());

        verify(userIdentityService).getAuthenticatedUsername();
        verify(walletRepository).findByIdAndOwnerUsername(WALLET_ID, USERNAME);
    }

    @Test
    @Transactional(readOnly = true)
    void viewBalance_Throws_NotFound() {

        when(userIdentityService.getAuthenticatedUsername()).thenReturn(USERNAME);
        when(walletRepository.findByIdAndOwnerUsername(WALLET_ID, USERNAME))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> walletService.viewBalance(WALLET_ID)
        );

        assertEquals("Wallet not found or access denied", exception.getMessage());
        verify(userIdentityService).getAuthenticatedUsername();
        verify(walletRepository).findByIdAndOwnerUsername(WALLET_ID, USERNAME);
    }

    @Test
    @Transactional
    void deposit_Success() {

        Wallet wallet = createTestWallet();
        BigDecimal depositAmount = BigDecimal.valueOf(500);
        BigDecimal expectedBalance = INITIAL_BALANCE.add(depositAmount);

        when(userIdentityService.getAuthenticatedUsername()).thenReturn(USERNAME);
        when(walletRepository.findByIdAndOwnerUsername(WALLET_ID, USERNAME))
                .thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = walletService.deposit(WALLET_ID, new TransactionRequest(depositAmount));

        assertNotNull(response);
        assertEquals(WALLET_ID, response.walletId());
        assertEquals(expectedBalance, response.currentBalance());
        verify(walletRepository).save(wallet);
    }

    @Test
    @Transactional
    void deposit_WalletNotFound() {

        when(userIdentityService.getAuthenticatedUsername()).thenReturn(USERNAME);
        when(walletRepository.findByIdAndOwnerUsername(WALLET_ID, USERNAME))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> walletService.deposit(WALLET_ID, new TransactionRequest(BigDecimal.TEN))
        );

        verify(walletRepository, never()).save(any());
    }

    @Test
    @Transactional
    void withdraw_Success() {

        Wallet wallet = createTestWallet();
        BigDecimal withdrawAmount = BigDecimal.valueOf(500);
        BigDecimal expectedBalance = INITIAL_BALANCE.subtract(withdrawAmount);

        when(userIdentityService.getAuthenticatedUsername()).thenReturn(USERNAME);
        when(walletRepository.findByIdAndOwnerUsername(WALLET_ID, USERNAME))
                .thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = walletService.withdraw(WALLET_ID, new TransactionRequest(withdrawAmount));

        assertNotNull(response);
        assertEquals(WALLET_ID, response.walletId());
        assertEquals(expectedBalance, response.currentBalance());
        verify(walletRepository).save(wallet);
    }

    @Test
    @Transactional
    void withdraw_Throws_InsufficientFunds() {

        Wallet wallet = createTestWallet();
        BigDecimal withdrawAmount = BigDecimal.valueOf(2000); // More than initial balance

        when(userIdentityService.getAuthenticatedUsername()).thenReturn(USERNAME);
        when(walletRepository.findByIdAndOwnerUsername(WALLET_ID, USERNAME))
                .thenReturn(Optional.of(wallet));

        assertThrows(
                InsufficientFundsException.class,
                () -> walletService.withdraw(WALLET_ID, new TransactionRequest(withdrawAmount))
        );

        verify(walletRepository, never()).save(any());
    }

    @Test
    @Transactional
    void withdraw_WalletNotFound() {

        when(userIdentityService.getAuthenticatedUsername()).thenReturn(USERNAME);
        when(walletRepository.findByIdAndOwnerUsername(WALLET_ID, USERNAME))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> walletService.withdraw(WALLET_ID, new TransactionRequest(BigDecimal.TEN))
        );

        verify(walletRepository, never()).save(any());
    }

//    @Test
//    @Transactional
//    void deposit_ShouldReturnTransactionResponse_WhenWalletExists() {
//
//        Long userId = 1L;
//        User mockUser = User.builder().id(1L).name("pesho").email("pesho@test.com").build();
//
//        BigDecimal depositAmount = BigDecimal.valueOf(50.0);
//        BigDecimal initialBalance = BigDecimal.valueOf(100.0);
//        BigDecimal finalBalance = initialBalance.add(depositAmount);
//
//        Wallet mockWallet = new Wallet();
//        mockWallet.setId(1L);
//        mockWallet.setUser(mockUser);
//        mockWallet.setBalance(initialBalance);
//
//        when(repository.findByUserId(userId)).thenReturn(Optional.of(mockWallet));
//        //return the exact Wallet object passed to the save method without modifying it.
//        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        TransactionRequest request = new TransactionRequest(depositAmount);
//
//        TransactionResponse response = walletService.deposit(userId, request);
//
//        assertNotNull(response);
//        assertEquals(1L, response.walletId());
//        assertEquals(userId, response.userId());
//        assertEquals(initialBalance, response.previousBalance());
//        assertEquals(finalBalance, response.currentBalance());
//        assertEquals(TransactionType.DEPOSIT, response.transactionType());
//        assertEquals(depositAmount, response.transactionAmount());
//
//        verify(repository, times(1)).findByUserId(userId);
//        verify(repository, times(1)).save(any(Wallet.class));
//    }
//
//    @Test
//    @Transactional
//    void deposit_ShouldThrowNotFoundException_WhenWalletDoesNotExist() {
//        Long userId = 1L;
//        BigDecimal depositAmount = BigDecimal.valueOf(50.0);
//
//        when(repository.findByUserId(userId)).thenReturn(Optional.empty());
//
//        TransactionRequest request = new TransactionRequest(depositAmount);
//
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
//            walletService.deposit(userId, request);
//        });
//
//        assertEquals("Wallet not found for user: " + userId, exception.getMessage());
//
//        verify(repository, times(1)).findByUserId(userId);
//        verify(repository, never()).save(any(Wallet.class));
//    }
//
//    @Test
//    public void depositUnauthorized_ShouldThrowUnauthorizedOperationException() {
//        Long userId = 1L;
//        Long walletUserId = 2L; // A different user id than the one requesting
//        User user = User.builder().id(userId).build();
//
//        User walletOwner = User.builder().id(walletUserId).build();
//        Wallet wallet = new Wallet(1L, BigDecimal.valueOf(100.00), walletOwner);
//
//        TransactionRequest request = new TransactionRequest(BigDecimal.valueOf(50));
//
//        when(repository.findByUserId(userId)).thenReturn(Optional.of(wallet));
//
//        // Should throw UnauthorizedOperationException because the wallet doesn't belong to the user
//        assertThrows(UnauthorizedOperationException.class, () -> walletService.deposit(userId, request));
//    }
//
//    @Test
//    @Transactional
//    void withdraw_ShouldReturnTransactionResponse_WhenSufficientFunds() {
//
//        Long userId = 1L;
//        User mockUser = User.builder().id(1L).name("pesho").email("pesho@test.com").build();
//
//        BigDecimal withdrawalAmount = BigDecimal.valueOf(50.0);
//        BigDecimal initialBalance = BigDecimal.valueOf(100.0);
//        BigDecimal finalBalance = initialBalance.subtract(withdrawalAmount);
//
//        Wallet mockWallet = new Wallet();
//        mockWallet.setId(1L);
//        mockWallet.setUser(mockUser);
//        mockWallet.setBalance(initialBalance);
//
//        when(repository.findByUserId(userId)).thenReturn(Optional.of(mockWallet));
//        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        TransactionRequest request = new TransactionRequest(withdrawalAmount);
//
//        TransactionResponse response = walletService.withdraw(userId, request);
//
//        assertNotNull(response);
//        assertEquals(1L, response.walletId());
//        assertEquals(userId, response.userId());
//        assertEquals(initialBalance, response.previousBalance());
//        assertEquals(finalBalance, response.currentBalance());
//        assertEquals(TransactionType.WITHDRAWAL, response.transactionType());
//        assertEquals(withdrawalAmount, response.transactionAmount());
//
//        verify(repository, times(1)).findByUserId(userId);
//        verify(repository, times(1)).save(any(Wallet.class));
//    }
//
//    @Test
//    @Transactional
//    void withdraw_ShouldThrowNotFoundException_WhenWalletDoesNotExist() {
//        Long userId = 1L;
//        BigDecimal withdrawalAmount = BigDecimal.valueOf(50.0);
//
//        when(repository.findByUserId(userId)).thenReturn(Optional.empty());
//
//        TransactionRequest request = new TransactionRequest(withdrawalAmount);
//
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
//            walletService.withdraw(userId, request);
//        });
//
//        assertEquals("Wallet not found for user: " + userId, exception.getMessage());
//
//        verify(repository, times(1)).findByUserId(userId);
//        verify(repository, never()).save(any(Wallet.class));
//    }
//
//    @Test
//    @Transactional
//    void withdraw_ShouldThrowInsufficientFundsException_WhenInsufficientFunds() {
//
//        Long userId = 1L;
//        User mockUser = User.builder().id(1L).name("pesho").email("pesho@test.com").build();
//
//        BigDecimal withdrawalAmount = BigDecimal.valueOf(150.0);
//        BigDecimal initialBalance = BigDecimal.valueOf(100.0);
//
//        Wallet mockWallet = new Wallet();
//        mockWallet.setId(1L);
//        mockWallet.setUser(mockUser);
//        mockWallet.setBalance(initialBalance);
//
//        when(repository.findByUserId(userId)).thenReturn(Optional.of(mockWallet));
//
//        TransactionRequest request = new TransactionRequest(withdrawalAmount);
//
//        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> {
//            walletService.withdraw(userId, request);
//        });
//
//        assertEquals("Insufficient funds. Current balance: 100.0 , Withdrawal amount : 150.0", exception.getMessage());
//
//        verify(repository, times(1)).findByUserId(userId);
//        verify(repository, never()).save(any(Wallet.class));
//    }
//
//    @Test
//    public void withdrawUnauthorized_ShouldThrowUnauthorizedOperationException() {
//        Long userId = 1L;
//        Long walletUserId = 2L; // A different user id than the one requesting
//        User user = User.builder().id(userId).build();
//
//        User walletOwner = User.builder().id(walletUserId).build();
//        Wallet wallet = new Wallet(1L, BigDecimal.valueOf(100.00), walletOwner);
//
//        TransactionRequest request = new TransactionRequest(BigDecimal.valueOf(50));
//
//        when(repository.findByUserId(userId)).thenReturn(Optional.of(wallet));
//
//        // Should throw UnauthorizedOperationException because the wallet doesn't belong to the user
//        assertThrows(UnauthorizedOperationException.class, () -> walletService.withdraw(userId, request));
//    }

}