package com.kris.wall_e.service;

import com.kris.wall_e.dto.CreateWalletRequest;
import com.kris.wall_e.entity.Wallet;

public interface WalletService {

    public Wallet createWallet(CreateWalletRequest request);

}
