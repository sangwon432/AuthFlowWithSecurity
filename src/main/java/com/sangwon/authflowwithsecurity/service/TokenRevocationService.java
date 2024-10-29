package com.sangwon.authflowwithsecurity.service;

import com.sangwon.authflowwithsecurity.utilty.CacheManager;
import com.sangwon.authflowwithsecurity.utilty.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenRevocationService {
    private final JwtUtility jwtUtility;
    private final CacheManager cacheManager;
    private final HttpServletRequest httpServletRequest;

    public void revoke() {
        final var authHeader = Optional.ofNullable(httpServletRequest.getHeader("Authorization")).orElseThrow(IllegalStateException::new);
        final var jti = jwtUtility.getJti(authHeader);
        final var ttl = jwtUtility.getTimeUntilExpiration(authHeader);

        // 캐시에 저장
        cacheManager.save(jti, ttl);

    }

    public boolean isRevoked(@NonNull final String authHeader) {
        final var jti = jwtUtility.getJti(authHeader);

        // 캐시에서 확인
        return cacheManager.isPresent(jti);
    }
}
