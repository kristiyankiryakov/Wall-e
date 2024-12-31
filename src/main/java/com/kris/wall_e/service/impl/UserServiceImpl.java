package com.kris.wall_e.service.impl;

import com.kris.wall_e.entity.User;
import com.kris.wall_e.exception.NotFoundException;
import com.kris.wall_e.repository.UserRepository;
import com.kris.wall_e.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public User getUserByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("user with username %s does not exist.".formatted(username)));
    }

}
