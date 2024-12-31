package com.kris.wall_e.service;

import com.kris.wall_e.dto.TransactionRequest;
import com.kris.wall_e.dto.TransactionResponse;
import com.kris.wall_e.dto.WalletRequest;
import com.kris.wall_e.dto.WalletResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface WalletService {

    public WalletResponse createWallet(WalletRequest request);

    public WalletResponse viewBalance(Long walletId);
//
//    public TransactionResponse deposit(Long userId, TransactionRequest transactionRequest);
//
//    public TransactionResponse withdraw(Long userId, TransactionRequest transactionRequest);
}
