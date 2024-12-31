package com.kris.wall_e.repository;

import com.kris.wall_e.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    boolean existsByNameAndUserUsername(String name, String username);
}
