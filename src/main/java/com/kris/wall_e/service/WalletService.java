package com.kris.wall_e.service;

import com.kris.wall_e.dto.TransactionRequest;
import com.kris.wall_e.dto.TransactionResponse;
import com.kris.wall_e.dto.WalletResponse;

public interface WalletService {

    public WalletResponse createWallet(Long userId);

    public WalletResponse getWallet(Long userId);

    public TransactionResponse deposit(Long userId, TransactionRequest transactionRequest);
}
