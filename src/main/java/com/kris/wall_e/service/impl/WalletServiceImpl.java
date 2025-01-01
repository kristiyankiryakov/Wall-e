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
import com.kris.wall_e.exception.InsufficientFundsException;
import com.kris.wall_e.repository.WalletRepository;
import com.kris.wall_e.service.UserService;
import com.kris.wall_e.service.WalletOperationService;
import com.kris.wall_e.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserService userService;
    private final UserIdentityService userIdentityService;
    private final WalletOperationService walletOperationService;

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

        Wallet wallet = walletOperationService.getAuthenticatedUserWallet(walletId);

        return new WalletResponse(
                wallet.getId(),
                wallet.getName(),
                wallet.getOwner().getUsername(),
                wallet.getBalance()
        );

    }

    @Override
    @Transactional
    //commit only if all operations inside the method are successful, otherwise roll back everything
    public TransactionResponse deposit(Long walletId, TransactionRequest request) {
        log.info("Attempting deposit of {} to wallet {}", request.amount(), walletId);

        Wallet wallet = walletOperationService.getAuthenticatedUserWallet(walletId);

        BigDecimal amount = request.amount();
        BigDecimal previousBalance = wallet.getBalance();

        processDeposit(wallet, amount);

        processTransaction(wallet, amount, TransactionType.DEPOSIT);

        // Save both wallet (for balance update) and transaction in one go
        wallet = walletRepository.save(wallet);  // This will also save the transaction due to cascade = ALL

        return new TransactionResponse(
                walletId,
                wallet.getOwner().getUsername(),
                previousBalance,
                wallet.getBalance(),
                TransactionType.DEPOSIT,
                amount
        );
    }

    private void processDeposit(Wallet wallet, BigDecimal amount) {
        wallet.setBalance(wallet.getBalance().add(amount));
    }

    @Override
    @Transactional
    //commit only if all operations inside the method are successful, otherwise roll back everything
    public TransactionResponse withdraw(Long walletId, TransactionRequest request) {
        log.info("Attempting withdraw of {} from wallet {}", request.amount(), walletId);

        Wallet wallet = walletOperationService.getAuthenticatedUserWallet(walletId);

        BigDecimal amount = request.amount();
        BigDecimal previousBalance = wallet.getBalance();

        processWithdraw(wallet, amount);

        processTransaction(wallet, amount, TransactionType.WITHDRAWAL);

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

    private void processWithdraw(Wallet wallet, BigDecimal amount) {

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Current balance: %s , Withdrawal amount : %s".formatted(wallet.getBalance(), amount)
            );
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));

    }

    private void checkIfWalletNameExists(String walletName, String currentUsername) {
        if (walletRepository.existsByNameAndOwnerUsername(walletName, currentUsername)) {
            throw new AlreadyExistsException("Wallet with name %s already exists.".formatted(walletName));
        }
    }

    private void processTransaction(Wallet wallet, BigDecimal amount, TransactionType type) {
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(type)
                .build();

        wallet.addTransaction(transaction);
    }

}
