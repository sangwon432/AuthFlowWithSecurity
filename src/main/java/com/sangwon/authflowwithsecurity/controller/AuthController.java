package com.sangwon.authflowwithsecurity.controller;

import com.sangwon.authflowwithsecurity.dto.TokenSuccessResponseDto;
import com.sangwon.authflowwithsecurity.dto.UserLoginRequestDto;
import com.sangwon.authflowwithsecurity.service.AuthService;
import com.sangwon.authflowwithsecurity.utilty.PublicEndpoint;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor()
@RequestMapping(value = "api/auth")
@Tag(name = "Authentication", description = "Endpoint for User Authentication and Token Management")
public class AuthController {

    private final AuthService authService;

    @PublicEndpoint
    @PostMapping(value="/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenSuccessResponseDto> loginUser(@Valid @RequestBody final UserLoginRequestDto userLoginRequestDto) {
        final var tokenResponse = authService.logInUser(userLoginRequestDto);
        return ResponseEntity.ok(tokenResponse);
    }
}
