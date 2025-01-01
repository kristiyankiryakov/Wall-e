package com.kris.wall_e.service;

import com.kris.wall_e.dto.TransactionRequest;
import com.kris.wall_e.dto.TransactionResponse;
import com.kris.wall_e.dto.WalletRequest;
import com.kris.wall_e.dto.WalletResponse;

public interface WalletService {

    public WalletResponse createWallet(WalletRequest request);

    public WalletResponse viewBalance(Long walletId);

    public TransactionResponse deposit(Long walletId, TransactionRequest transactionRequest);

    public TransactionResponse withdraw(Long walletId, TransactionRequest transactionRequest);
}
