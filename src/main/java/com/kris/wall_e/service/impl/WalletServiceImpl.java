package com.kris.wall_e.service.impl;

import com.kris.wall_e.dto.CreateWalletRequest;
import com.kris.wall_e.entity.Wallet;
import com.kris.wall_e.exception.WalletForUserAlreadyExistsException;
import com.kris.wall_e.repository.WalletRepository;
import com.kris.wall_e.service.UserService;
import com.kris.wall_e.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository repository;

    @Autowired
    private UserService userService;

    @Override
    public Wallet createWallet(CreateWalletRequest request) {

        //Ensures we have the user
        userService.getUser(request.userId());

        //Check if wallet already exists for user
        if (repository.existsByUserId(request.userId())) {
            throw new WalletForUserAlreadyExistsException("user with id %s already has a wallet.".formatted(request.userId()));
        }

        Wallet wallet = new Wallet();
        wallet.setUserId(request.userId());
        return repository.save(wallet);
    }


}
