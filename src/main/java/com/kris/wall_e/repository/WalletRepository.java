package com.kris.wall_e.repository;

import com.kris.wall_e.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    boolean existsByNameAndOwnerUsername(String name, String username);

    /**
     * Finds a wallet by ID and owner's username.
     *
     * @param walletId the ID of the wallet
     * @param username the username of the owner
     * @return the wallet if found
     */
    @Query("SELECT w FROM Wallet w WHERE w.id = :walletId AND w.owner.username = :username")
    Optional<Wallet> findByIdAndOwnerUsername(@Param("walletId") Long walletId, @Param("username") String username);

}
