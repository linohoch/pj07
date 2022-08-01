package com.example.pj0701.security.handler;

import com.example.pj0701.security.jwt.JwtTokenProvider;
import com.example.pj0701.security.userInfo.AuthUserInfo;
import com.example.pj0701.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AuthUserInfo userDetails = (AuthUserInfo) authentication.getPrincipal();
        log.info("getUserNo // {}",userDetails.getUserNo());
        log.info("getUsername // {}",userDetails.getUsername());
        log.info("getAuthorities // {}",userDetails.getAuthorities());
        Map<String,String> tokens =jwtTokenProvider.createTokens("Pj07",userDetails.getUsername());
//        response.setHeader("access_token", tokens.get("accessToken"));
//        response.setHeader("refresh_token", tokens.get("refreshToken"));
        CookieUtil.addCookie(response, "access_token", tokens.get("accessToken"), JwtTokenProvider.ACCESS_AGE);
        CookieUtil.addCookie(response, "refresh_token", tokens.get("refreshToken"), JwtTokenProvider.REFRESH_AGE);
        Cookie user = new Cookie("userNo",String.valueOf(userDetails.getUserNo()));
        user.setPath("/");
        response.addCookie(user);
        response.sendRedirect("/");

        //responseCookie
//        ResponseCookie responseCookie = ResponseCookie.from("access_token",tokens.get("accessToken")).build();

//        //json
//        response.setContentType("application/json");
//        try(PrintWriter out = response.getWriter()){
//            out.write(new JSONObject(tokens).toString());
////            out.flush();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }
}
