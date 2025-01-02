package com.kris.wall_e.service.impl;

import com.kris.wall_e.dto.TransactionRequest;
import com.kris.wall_e.dto.TransactionResponse;
import com.kris.wall_e.dto.WalletRequest;
import com.kris.wall_e.dto.WalletResponse;
import com.kris.wall_e.entity.Transaction;
import com.kris.wall_e.entity.User;
import com.kris.wall_e.entity.Wallet;
import com.kris.wall_e.enums.TransactionType;
import com.kris.wall_e.exception.AlreadyExistsException;
import com.kris.wall_e.exception.BaseBusinessException;
import com.kris.wall_e.exception.InsufficientFundsException;
import com.kris.wall_e.exception.ResourceNotFoundException;
import com.kris.wall_e.repository.TransactionRepository;
import com.kris.wall_e.repository.WalletRepository;
import com.kris.wall_e.service.UserService;
import com.kris.wall_e.service.WalletOperationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Mock
    private WalletOperationService walletOperationService;

    @Mock
    private TransactionRepository transactionRepository;

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

        when(walletRepository.save(any(Wallet.class))).thenReturn(new Wallet(1L, BigDecimal.ZERO, WALLET_NAME, owner, new ArrayList<>()));

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

        when(walletOperationService.getAuthenticatedUserWallet(WALLET_ID)).thenReturn(wallet);

        WalletResponse response = walletService.viewBalance(WALLET_ID);

        assertNotNull(response);
        assertEquals(WALLET_ID, response.id());
        assertEquals(WALLET_NAME, response.name());
        assertEquals(USERNAME, response.owner());
        assertEquals(INITIAL_BALANCE, response.balance());

        verify(walletOperationService).getAuthenticatedUserWallet(WALLET_ID);
    }

    @Test
    @Transactional(readOnly = true)
    void viewBalance_Throws_NotFound() {

        when(walletOperationService.getAuthenticatedUserWallet(WALLET_ID)).thenThrow(new ResourceNotFoundException("Wallet not found or access denied"));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            walletService.viewBalance(WALLET_ID);
        });

        assertEquals("Wallet not found or access denied", exception.getMessage());
    }

    @Test
    @Transactional
    void deposit_Success() {

        Wallet wallet = createTestWallet();
        BigDecimal depositAmount = BigDecimal.valueOf(500);
        BigDecimal expectedBalance = INITIAL_BALANCE.add(depositAmount);

        when(walletOperationService.getAuthenticatedUserWallet(WALLET_ID)).thenReturn(wallet);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> {
            Wallet savedWallet = invocation.getArgument(0);

            assertNotNull(savedWallet.getTransactions());
            assertFalse(savedWallet.getTransactions().isEmpty());
            return savedWallet;
        });

        TransactionResponse response = walletService.deposit(WALLET_ID, new TransactionRequest(depositAmount));

        assertNotNull(response);
        assertEquals(WALLET_ID, response.walletId());
        assertEquals(expectedBalance, response.currentBalance());

        verify(walletRepository).save(wallet);

        assertEquals(1, wallet.getTransactions().size());
        Transaction savedTransaction = wallet.getTransactions().get(0);
        assertEquals(wallet, savedTransaction.getWallet());
        assertEquals(TransactionType.DEPOSIT, savedTransaction.getType());
        assertEquals(depositAmount, savedTransaction.getAmount());
    }


    @Test
    @Transactional
    void withdraw_Success() {

        Wallet wallet = createTestWallet();
        BigDecimal withdrawAmount = BigDecimal.valueOf(500);
        BigDecimal expectedBalance = INITIAL_BALANCE.subtract(withdrawAmount);

        when(walletOperationService.getAuthenticatedUserWallet(WALLET_ID)).thenReturn(wallet);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> {
            Wallet savedWallet = invocation.getArgument(0);

            assertNotNull(savedWallet.getTransactions());
            assertFalse(savedWallet.getTransactions().isEmpty());
            return savedWallet;
        });


        TransactionResponse response = walletService.withdraw(WALLET_ID, new TransactionRequest(withdrawAmount));

        assertNotNull(response);
        assertEquals(WALLET_ID, response.walletId());
        assertEquals(expectedBalance, response.currentBalance());

        verify(walletRepository).save(wallet);

        assertEquals(1, wallet.getTransactions().size());
        Transaction savedTransaction = wallet.getTransactions().get(0);
        assertEquals(wallet, savedTransaction.getWallet());
        assertEquals(TransactionType.WITHDRAWAL, savedTransaction.getType());
        assertEquals(withdrawAmount, savedTransaction.getAmount());
    }

    @Test
    @Transactional
    void withdraw_Throws_InsufficientFunds() {

        Wallet wallet = createTestWallet();
        BigDecimal withdrawAmount = BigDecimal.valueOf(2000);

        when(walletOperationService.getAuthenticatedUserWallet(WALLET_ID)).thenThrow(new InsufficientFundsException(""));

        assertThrows(
                InsufficientFundsException.class,
                () -> walletService.withdraw(WALLET_ID, new TransactionRequest(withdrawAmount))
        );

        verify(walletRepository, never()).save(any());
    }

}