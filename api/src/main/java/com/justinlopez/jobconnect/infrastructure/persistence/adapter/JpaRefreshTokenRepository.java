package com.justinlopez.jobconnect.infrastructure.persistence.adapter;

import com.justinlopez.jobconnect.domain.model.RefreshToken;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.domain.repository.RefreshTokenRepository;
import com.justinlopez.jobconnect.infrastructure.persistence.mapper.RefreshTokenMapper;
import com.justinlopez.jobconnect.infrastructure.persistence.repository.JpaRefreshTokenRepositoryInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaRefreshTokenRepository implements RefreshTokenRepository {

    private final JpaRefreshTokenRepositoryInterface repository;
    private final RefreshTokenMapper mapper;

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return this.repository.findByTokenHash(tokenHash)
                .map(mapper::toDomain);
    }

    @Override
    public List<RefreshToken> findByUserId(UserId userId) {
        return this.repository.findByUserId(userId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return mapper.toDomain(this.repository.save(mapper.toEntity(refreshToken)));
    }

    @Override
    public void deleteByTokenHash(String tokenHash) {
        this.repository.deleteByTokenHash(tokenHash);
    }

}