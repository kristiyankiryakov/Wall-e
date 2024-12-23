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
import com.kris.wall_e.exception.UnauthorizedOperationException;
import com.kris.wall_e.mapper.UserMapper;
import com.kris.wall_e.repository.WalletRepository;
import com.kris.wall_e.service.UserService;
import com.kris.wall_e.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository repository;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public WalletResponse createWallet(Long userId) {

        //Ensures we have the user
        User user = userService.getUser(userId);

        //Check if wallet already exists for user
        if (repository.existsByUserId(userId)) {
            throw new AlreadyExistsException("user with id %s already has a wallet.".formatted(userId));
        }

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        repository.save(wallet);

        UserResponseDto userResponseDto = userMapper.fromUser(user);
        return new WalletResponse(
                userResponseDto,
                wallet.getBalance()
        );

    }

    @Override
    public WalletResponse getWallet(Long userId) {

        User user = userService.getUser(userId);
        UserResponseDto userResponseDto = userMapper.fromUser(user);

        Wallet wallet = repository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet not found for user: " + userId));

        if (!wallet.getUser().getId().equals(userId)) {
            throw new UnauthorizedOperationException("User is not authorized view this wallet.");
        }

        return new WalletResponse(
                userResponseDto,
                wallet.getBalance()
        );

    }

    @Override
    @Transactional
    public TransactionResponse deposit(Long userId, TransactionRequest request) {
        BigDecimal amount = request.amount();

        Wallet wallet = repository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet not found for user: " + userId));

        if (!wallet.getUser().getId().equals(userId)) {
            throw new UnauthorizedOperationException("User is not authorized to deposit into this wallet.");
        }

        BigDecimal previousBalance = wallet.getBalance();

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet = repository.save(wallet);

        return new TransactionResponse(
                wallet.getId(),
                userId,
                previousBalance,
                wallet.getBalance(),
                TransactionType.DEPOSIT,
                amount
        );
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(Long userId, TransactionRequest request) {
        BigDecimal amount = request.amount();

        Wallet wallet = repository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet not found for user: " + userId));

        if (!wallet.getUser().getId().equals(userId)) {
            throw new UnauthorizedOperationException("User is not authorized to withdraw from this wallet.");
        }

        BigDecimal previousBalance = wallet.getBalance();


        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Current balance: %s , Withdrawal amount : %s".formatted(wallet.getBalance(), amount)
            );
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        repository.save(wallet);

        return new TransactionResponse(
                wallet.getId(),
                userId,
                previousBalance,
                wallet.getBalance(),
                TransactionType.WITHDRAWAL,
                amount
        );
    }

}
