package com.kris.wall_e.controller;

import com.kris.wall_e.dto.TransactionDto;
import com.kris.wall_e.dto.WalletResponse;
import com.kris.wall_e.entity.Transaction;
import com.kris.wall_e.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{walletId}")
    public ResponseEntity<List<TransactionDto>> getTransactions(@PathVariable("walletId") Long walletId) {
        return ResponseEntity.ok(transactionService.getTransactionsForWallet(walletId));
    }


}
