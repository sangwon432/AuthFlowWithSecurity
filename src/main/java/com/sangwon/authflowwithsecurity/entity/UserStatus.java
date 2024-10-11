package com.sangwon.authflowwithsecurity.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum UserStatus {

    PENDING_APPROVAL("Pending Approval", List.of("userprofile.read", "userprofile.update", "useridentity.verify")),
    APPROVED("Approved", List.of("fullaccess")),
    DEACTIVAED("Deactivated", List.of("userprofile.read"));

    private final String value;
    private final List<String> scopes;
}
