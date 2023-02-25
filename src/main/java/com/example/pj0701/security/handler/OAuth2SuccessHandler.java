package com.example.pj0701.security.handler;

import com.example.pj0701.proc.UserMapper;
import com.example.pj0701.security.jwt.JwtTokenProvider;
import com.example.pj0701.security.service.CustomUserService;
import com.example.pj0701.security.userInfo.AuthUserInfo;
import com.example.pj0701.security.userInfo.OAuth2Attributes;
import com.example.pj0701.util.CookieUtil;
import com.example.pj0701.vo.Pj07UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    JwtTokenProvider jwtTokenProvider;

    OAuth2SuccessHandler(JwtTokenProvider jwtTokenProvider){
        this.jwtTokenProvider=jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException, ServletException {


        log.info("success핸들러가 받은 auth//{}",auth);
        AuthUserInfo oAuth2User = (AuthUserInfo) auth.getPrincipal();
        log.info("success핸들러가 getPrincipal//{}",auth.getPrincipal());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        Map<String,String> tokens = jwtTokenProvider.createTokens(oAuth2User.getProvider(), oAuth2User.getEmail());
        CookieUtil.addCookie(response,"access_token", tokens.get("accessToken"),1000*60*60);
        response.sendRedirect("/");

    //3.쿠키처리
        //참고: 쿼링파람에 담는 예제
//        Token token = tokenService.generateToken(userDto.getEmail(), "USER");
//        log.info("{}", token);
//        String targetUrl = UriComponentsBuilder.fromUriString("/home")
//                .queryParam("token", token)
//                .build().toUriString();

//        String targetUrl=determineTargetUrl(request, response, auth);
//        targetUrl="http://localhost:8080/board/list";
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }


}
