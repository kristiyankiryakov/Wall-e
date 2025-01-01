package com.kris.wall_e.service;

import com.kris.wall_e.dto.TransactionHistoryRequest;
import com.kris.wall_e.entity.Transaction;

import java.util.List;

public interface TransactionService {

    List<Transaction> getTransactionsForWallet(Long walletId);

    Transaction createTransaction(Long walletId, TransactionHistoryRequest transactionRequest);

}
