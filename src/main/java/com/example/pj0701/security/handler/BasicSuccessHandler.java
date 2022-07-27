package com.example.pj0701.security.handler;

import com.example.pj0701.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BasicSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        //
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        log.info("getUsername//{}",userDetails.getUsername());
        log.info("getAuthorities//{}",userDetails.getAuthorities());
        Map<String,String> tokens =jwtTokenProvider.createTokens("Pj07",userDetails.getUsername());
        response.setHeader("access_token", tokens.get("accessToken"));
        response.setHeader("refresh_token", tokens.get("refreshToken"));


    }
}
