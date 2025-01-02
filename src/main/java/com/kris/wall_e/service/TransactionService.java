package com.kris.wall_e.service;

import com.kris.wall_e.dto.TransactionDto;
import com.kris.wall_e.dto.TransactionHistoryRequest;
import com.kris.wall_e.entity.Transaction;

import java.util.List;

public interface TransactionService {

    List<TransactionDto> getTransactionsForWallet(Long walletId);

    Transaction createTransaction(Long walletId, TransactionHistoryRequest transactionRequest);

}
