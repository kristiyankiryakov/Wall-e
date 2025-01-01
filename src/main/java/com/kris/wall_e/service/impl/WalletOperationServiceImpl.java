package com.kris.wall_e.service.impl;

import com.kris.wall_e.entity.Wallet;
import com.kris.wall_e.exception.ResourceNotFoundException;
import com.kris.wall_e.repository.WalletRepository;
import com.kris.wall_e.service.WalletOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletOperationServiceImpl implements WalletOperationService {

    @Autowired
    private UserIdentityService userIdentityService;

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Wallet getAuthenticatedUserWallet(Long walletId) throws ResourceNotFoundException {
        String username = userIdentityService.getAuthenticatedUsername();
        return walletRepository.findByIdAndOwnerUsername(walletId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found or access denied"));
    }

}
