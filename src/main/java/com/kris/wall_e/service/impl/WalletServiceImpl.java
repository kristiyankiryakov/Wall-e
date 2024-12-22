package com.kris.wall_e.service.impl;

import com.kris.wall_e.dto.UserResponseDto;
import com.kris.wall_e.dto.WalletResponse;
import com.kris.wall_e.entity.User;
import com.kris.wall_e.entity.Wallet;
import com.kris.wall_e.exception.NotFoundException;
import com.kris.wall_e.exception.WalletForUserAlreadyExistsException;
import com.kris.wall_e.mapper.UserMapper;
import com.kris.wall_e.repository.WalletRepository;
import com.kris.wall_e.service.UserService;
import com.kris.wall_e.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository repository;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public WalletResponse createWallet(Long userId) {

        //Ensures we have the user
        User user = userService.getUser(userId);

        //Check if wallet already exists for user
        if (repository.existsByUserId(userId)) {
            throw new WalletForUserAlreadyExistsException("user with id %s already has a wallet.".formatted(userId));
        }

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        repository.save(wallet);

        UserResponseDto userResponseDto = userMapper.fromUser(user);
        return new WalletResponse(
                userResponseDto,
                wallet.getBalance()
        );

    }

    @Override
    public WalletResponse getWallet(Long userId) {

        User user = userService.getUser(userId);
        UserResponseDto userResponseDto = userMapper.fromUser(user);

        Wallet wallet = repository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("user with id: %s doesn't have a wallet.".formatted(userId)));

        return new WalletResponse(
                userResponseDto,
                wallet.getBalance()
        );

    }


}
