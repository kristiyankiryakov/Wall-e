package com.kris.wall_e.controller;

import com.kris.wall_e.dto.CreateWalletRequest;
import com.kris.wall_e.entity.Wallet;
import com.kris.wall_e.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@Validated @RequestBody CreateWalletRequest request) {
        Wallet wallet = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }

}
