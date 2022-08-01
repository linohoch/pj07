package com.example.pj0701.security.service;

import com.example.pj0701.security.service.CustomUserService;
import com.example.pj0701.security.userInfo.AuthUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
//@RequiredArgsConstructor
public class BasicAuthenticationProvider implements AuthenticationProvider {

//    private final CustomUserService userService;
//    private final PasswordEncoder passwordEncoder;
    CustomUserService userService;
    PasswordEncoder passwordEncoder;
 BasicAuthenticationProvider(CustomUserService userService, PasswordEncoder passwordEncoder){
     this.userService=userService;
     this.passwordEncoder=passwordEncoder;
 }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
            String username=token.getName();
            String password=(String)token.getCredentials();
            log.info("basicProvider username//{}",username);
        AuthUserInfo dbUserInfo = userService.loadUserByUsername(username);
        log.info("{}",dbUserInfo);
        if( !passwordEncoder.matches(password, dbUserInfo.getPassword()) ){
            throw new BadCredentialsException("invalid password");
        }
        return new UsernamePasswordAuthenticationToken(dbUserInfo,
                                             null,
                                                        dbUserInfo.getAuthorities());
    }
    //this provider가 동작하는 token type
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
