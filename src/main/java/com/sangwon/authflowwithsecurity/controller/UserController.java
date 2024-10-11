package com.sangwon.authflowwithsecurity.controller;

import com.sangwon.authflowwithsecurity.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/user")
@Tag(name = "User Management" , description = "Endpoints for managing user details"
public class UserController {
    private final UserService userService;


}
