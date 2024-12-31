package com.kris.wall_e.service.impl;

import com.kris.wall_e.dto.TransactionRequest;
import com.kris.wall_e.dto.TransactionResponse;
import com.kris.wall_e.dto.WalletRequest;
import com.kris.wall_e.dto.WalletResponse;
import com.kris.wall_e.entity.User;
import com.kris.wall_e.entity.Wallet;
import com.kris.wall_e.enums.TransactionType;
import com.kris.wall_e.exception.AlreadyExistsException;
import com.kris.wall_e.exception.InsufficientFundsException;
import com.kris.wall_e.exception.ResourceNotFoundException;
import com.kris.wall_e.exception.UnauthorizedOperationException;
import com.kris.wall_e.repository.WalletRepository;
import com.kris.wall_e.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserServiceImpl userService;
    private final UserIdentityService userIdentityService;

    @Override
    public WalletResponse createWallet(WalletRequest request) {

        String username = userIdentityService.getAuthenticatedUsername();

        User owner = userService.getUserByUsername(username);

        checkIfWalletNameExists(request.walletName(), username);

        Wallet wallet = new Wallet();
        wallet.setOwner(owner);
        wallet.setName(request.walletName());

        walletRepository.save(wallet);

        return new WalletResponse(
                wallet.getId(),
                wallet.getName(),
                username,
                wallet.getBalance()
        );

    }

    @Override
    @Transactional(readOnly = true)
    public WalletResponse viewBalance(Long walletId) {

        Wallet wallet = getAuthenticatedUserWallet(walletId);

        return new WalletResponse(
                wallet.getId(),
                wallet.getName(),
                wallet.getOwner().getUsername(),
                wallet.getBalance()
        );

    }

    @Override
    @Transactional
    public TransactionResponse deposit(Long walletId, TransactionRequest request) {
        Wallet wallet = getAuthenticatedUserWallet(walletId);

        BigDecimal amount = request.amount();

        BigDecimal previousBalance = wallet.getBalance();

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet = walletRepository.save(wallet);

        return new TransactionResponse(
                walletId,
                wallet.getOwner().getUsername(),
                previousBalance,
                wallet.getBalance(),
                TransactionType.DEPOSIT,
                amount
        );
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(Long walletId, TransactionRequest request) {
        Wallet wallet = getAuthenticatedUserWallet(walletId);

        BigDecimal amount = request.amount();

        BigDecimal previousBalance = wallet.getBalance();

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Current balance: %s , Withdrawal amount : %s".formatted(wallet.getBalance(), amount)
            );
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        return new TransactionResponse(
                walletId,
                wallet.getOwner().getUsername(),
                previousBalance,
                wallet.getBalance(),
                TransactionType.WITHDRAWAL,
                amount
        );
    }

    private void checkIfWalletNameExists(String walletName, String currentUsername) {
        if (walletRepository.existsByNameAndOwnerUsername(walletName, currentUsername)) {
            throw new AlreadyExistsException("Wallet with name %s already exists.".formatted(walletName));
        }
    }

    private Wallet getAuthenticatedUserWallet(Long walletId) throws ResourceNotFoundException {
        String username = userIdentityService.getAuthenticatedUsername();

        return walletRepository.findByIdAndOwnerUsername(walletId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found or access denied"));
    }

}
