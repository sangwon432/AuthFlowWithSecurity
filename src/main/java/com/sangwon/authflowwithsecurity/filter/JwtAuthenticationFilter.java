package com.sangwon.authflowwithsecurity.filter;

import com.sangwon.authflowwithsecurity.exception.TokenVerificationException;
import com.sangwon.authflowwithsecurity.service.TokenRevocationService;
import com.sangwon.authflowwithsecurity.utilty.ApiEndpointSecurityInspector;
import com.sangwon.authflowwithsecurity.utilty.JwtUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtility jwtUtility;
    private final ApiEndpointSecurityInspector apiEndpointSecurityInspector;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final TokenRevocationService tokenRevocationService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final var unsecuredApiBeingInvolved = apiEndpointSecurityInspector.isUnsecureRequest(request);
        if (Boolean.FALSE.equals(unsecuredApiBeingInvolved)) {
            final var authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

            if (StringUtils.isNoneEmpty(authorizationHeader)) {
                if (authorizationHeader.startsWith(BEARER_PREFIX)) {
                    final var token = authorizationHeader.replace(BEARER_PREFIX, StringUtils.EMPTY);

                    final var isTokenRevoked = tokenRevocationService.isRevoked(token);
                    if (Boolean.TRUE.equals(isTokenRevoked)) {
                        throw new TokenVerificationException();
                    }

                    final var userId = jwtUtility.extractUserId(token);
                    final var authorities = jwtUtility.getAuthority(token);
                    final var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }
            }
        }
        filterChain.doFilter(request, response);
    }
}