package com.justinlopez.jobconnect.domain.repository;

import com.justinlopez.jobconnect.domain.model.RefreshToken;
import com.justinlopez.jobconnect.domain.model.vo.UserId;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUserId(UserId userId);

    RefreshToken save(RefreshToken refreshToken);

    void deleteByTokenHash(String tokenHash);

}