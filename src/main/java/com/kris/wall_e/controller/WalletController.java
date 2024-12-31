package com.kris.wall_e.controller;

import com.kris.wall_e.dto.WalletRequest;
import com.kris.wall_e.dto.WalletResponse;
import com.kris.wall_e.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody WalletRequest request) {
        return ResponseEntity.ok(walletService.createWallet(request));
    }

//    @GetMapping("/{walletId}")
//    public ResponseEntity<WalletResponse> viewBalance(@PathVariable("walletId") Long walletId) {
//        return ResponseEntity.ok(walletService.getWallet(walletId));
//    }

//    @PutMapping("/deposit/{userId}")
//    public ResponseEntity<TransactionResponse> deposit(@PathVariable("userId") Long userId, @Validated @RequestBody TransactionRequest request) {
//        return ResponseEntity.ok(walletService.deposit(userId, request));
//    }
//
//    @PutMapping("/withdraw/{userId}")
//    public ResponseEntity<TransactionResponse> withdraw(@PathVariable("userId") Long userId, @Validated @RequestBody TransactionRequest request) {
//        return ResponseEntity.ok(walletService.withdraw(userId, request));
//    }

}
