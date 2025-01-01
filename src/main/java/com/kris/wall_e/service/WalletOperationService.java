package com.kris.wall_e.service;

import com.kris.wall_e.entity.Wallet;
import com.kris.wall_e.exception.ResourceNotFoundException;

public interface WalletOperationService {

    Wallet getAuthenticatedUserWallet(Long walletId) throws ResourceNotFoundException;

}
