package com.example.pj0701.security.jwt;

import com.example.pj0701.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
//            final String token = parseBearerToken(request);
            final String token = CookieUtil.getCookieValue(request, "access_token");
            log.info("Parse jwt ... {}", !Objects.equals(token, "null"));
            if(!Objects.equals(token, "null")) {
                String userId = jwtTokenProvider.validateAccessToken(token);
                log.info("userId:{}",userId);
                Authentication authentication=jwtTokenProvider.getAuthentication(token);
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);
            }

        } catch (Exception ex){
            log.error("Could not set user authentic in security context //{}", ex.toString());
//            SecurityContextHolder.clearContext();
        }
            filterChain.doFilter(request, response);
    }
    //---------
    private String parseBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}