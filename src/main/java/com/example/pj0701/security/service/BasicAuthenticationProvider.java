package com.example.pj0701.security.service;

import com.example.pj0701.security.service.CustomUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class BasicAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
            String username=token.getName();
            String password=(String)token.getCredentials();
        UserDetails dbUserInfo = userService.loadUserByUsername(username);

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
