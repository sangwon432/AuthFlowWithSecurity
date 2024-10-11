package com.sangwon.authflowwithsecurity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class TokenVerificationException extends ResponseStatusException {

    private static final String DEFAULT_MESSAGE = "Authentication failure: Token missing. Invalid. revoked or expired";
    public TokenVerificationException() {
        super(HttpStatus.UNAUTHORIZED, DEFAULT_MESSAGE);
    }
}
