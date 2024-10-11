package com.sangwon.authflowwithsecurity.controller;

import com.sangwon.authflowwithsecurity.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor()
@RequestMapping(value = "api/auth")
@Tag(name = "Authentication", description = "Endpoint for User Authentication and Token Management")
public class AuthController {

    private final AuthService authService;
}
