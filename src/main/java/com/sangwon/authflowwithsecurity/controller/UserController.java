package com.sangwon.authflowwithsecurity.controller;

import com.sangwon.authflowwithsecurity.dto.UserCreationRequestDto;
import com.sangwon.authflowwithsecurity.service.UserService;
import com.sangwon.authflowwithsecurity.utilty.PublicEndpoint;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/user")
@Tag(name = "User Management" , description = "Endpoints for managing user details")
public class UserController {
    private final UserService userService;

    @PublicEndpoint
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> createUser(@Valid @RequestBody final UserCreationRequestDto userCreationRequestDto) {
        userService.createUser(userCreationRequestDto);
        System.out.println(userCreationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }




}
