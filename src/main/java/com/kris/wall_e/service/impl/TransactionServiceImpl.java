package com.kris.wall_e.service.impl;

import com.kris.wall_e.dto.TransactionHistoryRequest;
import com.kris.wall_e.entity.Transaction;
import com.kris.wall_e.entity.Wallet;
import com.kris.wall_e.repository.TransactionRepository;
import com.kris.wall_e.service.TransactionService;
import com.kris.wall_e.service.WalletOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletOperationService walletOperationService;

    @Override
    public Transaction createTransaction(Long walletId, TransactionHistoryRequest transactionRequest) {
        Wallet wallet = walletOperationService.getAuthenticatedUserWallet(walletId);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(transactionRequest.amount())
                .type(transactionRequest.type())
                .build();

        wallet.addTransaction(transaction);
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionsForWallet(Long walletId) {
        return transactionRepository.findByWalletId(walletId);
    }
}
