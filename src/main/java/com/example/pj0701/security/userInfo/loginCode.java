package com.example.pj0701.security.userInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum loginCode {
    NO_PARAM(-300,"잘못된입력"),
    DUPLICATE(-200,"다중조회"),
    NO_USER(-100, "조회없음"),
    SUCCESS(100,"로그인성공"),
    UNMATCHED(200,"비밀번호틀림"),
    BANNED(300, "정지회원");

    private final int code;
    private final String state;

}
