package com.javaapp.repository;

import com.javaapp.entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Integer> {

    Optional<ConfirmationToken> findByToken(String token);
}
