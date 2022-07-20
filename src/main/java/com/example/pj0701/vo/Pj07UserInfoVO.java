package com.example.pj0701.vo;

import com.example.pj0701.security.userInfo.Role;
import lombok.*;

import javax.validation.constraints.Email;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Pj07UserInfoVO {
    private String adminYn;
    private Role role;
    private String providerType;
    // user_agent
    private String msg;
    private String accessToken;
    private String refreshToken;
    //일단 임시

    private int userNo;
    @Email
    private String userId;
    //
    private String firstName;
    private String lastName;
    private String userBirthDate;
    private String userSex;
    private String joinDate;
    private String ipAddress;
    private String lastLogin;
    //
    private String pw;
    private String pwLastChange;
    private String pwEncryptYn;
    private String bannedYn;
    //
    private String bannedDate;
    private String bannedCode;
}
