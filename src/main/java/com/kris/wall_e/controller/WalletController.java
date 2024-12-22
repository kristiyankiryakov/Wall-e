package com.kris.wall_e.controller;

import com.kris.wall_e.dto.TransactionRequest;
import com.kris.wall_e.dto.TransactionResponse;
import com.kris.wall_e.dto.WalletResponse;
import com.kris.wall_e.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/{userId}")
    public ResponseEntity<WalletResponse> createWallet(@PathVariable("userId") Long userId) {
        WalletResponse wallet = walletService.createWallet(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponse> viewBalance(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(walletService.getWallet(userId));
    }

    @PostMapping("/deposit/{userId}")
    public ResponseEntity<TransactionResponse> deposit(@PathVariable("userId") Long userId, @Validated @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(walletService.deposit(userId, request));
    }

}
