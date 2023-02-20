package com.example.pj0701.security.handler;

import com.example.pj0701.proc.UserMapper;
import com.example.pj0701.security.jwt.JwtTokenProvider;
import com.example.pj0701.security.userInfo.OAuth2Attributes;
import com.example.pj0701.util.CookieUtil;
import com.example.pj0701.vo.Pj07UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
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

    UserMapper userMapper;
    JwtTokenProvider jwtTokenProvider;

    OAuth2SuccessHandler(JwtTokenProvider jwtTokenProvider, UserMapper userMapper){
        this.jwtTokenProvider=jwtTokenProvider;
        this.userMapper=userMapper;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException, ServletException {

        OAuth2Attributes oAuth2User = (OAuth2Attributes) auth.getPrincipal();
        Pj07UserInfoVO DBUser = Optional.ofNullable(userMapper.selectUserNoById(oAuth2User.getEmail()))
                                        .orElseGet(Pj07UserInfoVO::new);
        log.info("DB is null? // {}", DBUser);//userNo int로 해놔서 0넘어옴.
        int userNo = DBUser.getUserNo();
        boolean firstVisit= userNo == 0 ;
        log.info("firstVisit? // {}",firstVisit);
//    //1.등록처리
//        //DB 회원이 아닌 경우(최초로그인)
        if (firstVisit) {
            log.info("not signed email // {}", oAuth2User.getAttributes().get("email"));
            //소셜회원등록 진행
            Pj07UserInfoVO pj07UserInfoVO= Pj07UserInfoVO.builder()
                    .userId(oAuth2User.getEmail())
                    .firstName(oAuth2User.getFirstName())
                    .lastName(oAuth2User.getLastName())
                    .providerType(oAuth2User.getProvider())
                    .build();
            userNo = userMapper.createSocialUser(pj07UserInfoVO);
            log.info("createSocialUserNo{}",userNo);
        }
//        //DB 회원이지만 로그인방식이 다른 경우
//        if (DBUser.getProviderType() == null || !DBUser.getProviderType().equals(oAuth2User.getProvider())) {
//            log.info("miss matched signup type");
//            //어떻게 처리할지
//        }
//        //타임스탬프
            log.info("timestamp");
            userMapper.updateLoginTimestamp(DBUser.getUserNo());
    //2.토큰처리
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
