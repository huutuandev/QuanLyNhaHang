package com.restaurant.management.service;

import com.restaurant.management.models.RefreshTokenEntity;

import java.util.Optional;

public interface IRefreshTokenService {
    RefreshTokenEntity createRefreshToken(Long userId);
    RefreshTokenEntity verifyExpiration(RefreshTokenEntity token);
    Optional<RefreshTokenEntity> findByToken(String token);
    void revokeToken(String token);
    void deleteByUser(Long userId);
}
