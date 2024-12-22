package com.kris.wall_e.service;

import com.kris.wall_e.dto.WalletResponse;
import com.kris.wall_e.entity.Wallet;

public interface WalletService {

    public WalletResponse createWallet(Long userId);

    public WalletResponse getWallet(Long userId);
}
