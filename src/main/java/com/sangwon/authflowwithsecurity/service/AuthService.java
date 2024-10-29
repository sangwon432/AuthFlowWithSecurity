package com.sangwon.authflowwithsecurity.service;

import com.sangwon.authflowwithsecurity.configuration.TokenConfigurationProperties;
import com.sangwon.authflowwithsecurity.dto.TokenSuccessResponseDto;
import com.sangwon.authflowwithsecurity.dto.UserLoginRequestDto;
import com.sangwon.authflowwithsecurity.exception.InvalidCredentialsException;
import com.sangwon.authflowwithsecurity.repository.UserRepository;
import com.sangwon.authflowwithsecurity.utilty.JwtUtility;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(TokenConfigurationProperties.class)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompromisedPasswordChecker compromisedPasswordChecker;
    private final JwtUtility jwtUtility;

    public TokenSuccessResponseDto logInUser(@NonNull final UserLoginRequestDto userLoginRequestDto) {
        final var user = userRepository.findByEmailId(userLoginRequestDto.getEmailId())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid login credentials provided"));

        final var encodedPassword = userLoginRequestDto.getPassword();
        final var plainTextPassword = userLoginRequestDto.getPassword();
        final var isCorrectPassword = passwordEncoder.matches(plainTextPassword, encodedPassword);
        if (Boolean.FALSE.equals(isCorrectPassword)) {
            throw new InvalidCredentialsException("Invalid login credentials provided");
        }

        final var isPasswordCompromised = compromisedPasswordChecker.check(plainTextPassword).isCompromised();
        if (Boolean.TRUE.equals(isPasswordCompromised)) {
            throw new CompromisedPasswordException("Password has been compromised. Passsword reset required.");
        }

        // 토큰발급
        final var accessToken = jwtUtility.generateAccessToken(user);
        final var refreshToken = "123456789"; // temp

        return TokenSuccessResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

}
