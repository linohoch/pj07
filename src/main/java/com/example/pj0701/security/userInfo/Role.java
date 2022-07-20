package com.example.pj0701.security.userInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {
    USER("ROLE_USER","회원"),
    MEMBER("ROLE_MEMBER","회원"),
    ADMIN("ROLE_ADMIN","관리자");

    private final String code;
    private final String value;
}
