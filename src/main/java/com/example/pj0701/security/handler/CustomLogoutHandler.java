package com.example.pj0701.security.handler;

import com.example.pj0701.security.jwt.JwtTokenProvider;
import com.example.pj0701.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class CustomLogoutHandler implements LogoutHandler {
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        jwtTokenProvider.invalidateRefreshToken(Integer.parseInt(CookieUtil.getCookieValue(request,"userNo")));

        CookieUtil.deleteCookie(request,response,"access_token");
        CookieUtil.deleteCookie(request,response,"refresh_token");
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
    }
}
