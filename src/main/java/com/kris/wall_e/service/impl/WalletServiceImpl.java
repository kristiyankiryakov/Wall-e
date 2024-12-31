package com.kris.wall_e.service.impl;

import com.kris.wall_e.dto.WalletRequest;
import com.kris.wall_e.dto.WalletResponse;
import com.kris.wall_e.entity.User;
import com.kris.wall_e.entity.Wallet;
import com.kris.wall_e.exception.AlreadyExistsException;
import com.kris.wall_e.repository.WalletRepository;
import com.kris.wall_e.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserServiceImpl userService;
    private final UserIdentityService userIdentityService;

    public WalletResponse createWallet(WalletRequest request) {

        String username = userIdentityService.getAuthenticatedUsername();

        User user = userService.getUserByUsername(username);

        checkIfWalletNameExists(request.walletName(), username);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setName(request.walletName());

        walletRepository.save(wallet);

        return new WalletResponse(
                wallet.getName(),
                user.getUsername(),
                wallet.getBalance()
        );

    }

//    public WalletResponse getWallet(Long walletId) {
//
//
//
//    }
//
//    @Override
//    @Transactional
//    public TransactionResponse deposit(Long userId, TransactionRequest request) {
//        BigDecimal amount = request.amount();
//
//        Wallet wallet = getWalletByUserId(userId);
//
//        explicitOwnershipCheck(wallet, userId);
//
//        BigDecimal previousBalance = wallet.getBalance();
//
//        wallet.setBalance(wallet.getBalance().add(amount));
//        wallet = repository.save(wallet);
//
//        return new TransactionResponse(
//                wallet.getId(),
//                userId,
//                previousBalance,
//                wallet.getBalance(),
//                TransactionType.DEPOSIT,
//                amount
//        );
//    }
//
//    @Override
//    @Transactional
//    public TransactionResponse withdraw(Long userId, TransactionRequest request) {
//        BigDecimal amount = request.amount();
//
//        Wallet wallet = getWalletByUserId(userId);
//
//        explicitOwnershipCheck(wallet, userId);
//
//        BigDecimal previousBalance = wallet.getBalance();
//
//        if (wallet.getBalance().compareTo(amount) < 0) {
//            throw new InsufficientFundsException(
//                    "Insufficient funds. Current balance: %s , Withdrawal amount : %s".formatted(wallet.getBalance(), amount)
//            );
//        }
//
//        wallet.setBalance(wallet.getBalance().subtract(amount));
//        repository.save(wallet);
//
//        return new TransactionResponse(
//                wallet.getId(),
//                userId,
//                previousBalance,
//                wallet.getBalance(),
//                TransactionType.WITHDRAWAL,
//                amount
//        );
//    }

    private void checkIfWalletNameExists(String walletName, String currentUsername) {
        if (walletRepository.existsByNameAndUserUsername(walletName, currentUsername)) {
            throw new AlreadyExistsException("Wallet with name %s already exists.".formatted(walletName));
        }
    }


}
