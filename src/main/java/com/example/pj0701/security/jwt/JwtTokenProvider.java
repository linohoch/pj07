package com.example.pj0701.security.jwt;

import com.example.pj0701.proc.UserMapper;
import com.example.pj0701.security.service.CustomUserService;
import com.example.pj0701.security.userInfo.AuthUserInfo;
import com.example.pj0701.util.TimeUtil;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    public static final int CHECK_LIMIT = 1000 * 60 * 10;//10분
    public static final int ACCESS_AGE = 1000 * 60 * 60; //1시간;
    public static final int REFRESH_AGE = 1000 * 60 * 60 * 24 * 7; //7일
    private final UserMapper userMapper;
    private final CustomUserService customUserService;

    @Value("{jwt.key}")
    private String ACCESS_SECRET_KEY;
    @Value("{jwt.refresh}")
    private String REFRESH_SECRET_KEY;

    @PostConstruct
    protected void init(){
        ACCESS_SECRET_KEY= Base64.getEncoder().encodeToString(ACCESS_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        REFRESH_SECRET_KEY= Base64.getEncoder().encodeToString(REFRESH_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    //토큰 둘 다 발행
    public Map<String, String> createTokens(String provider, String userId){
        Date expiryDate = new Date(System.currentTimeMillis()+ACCESS_AGE);
        Date refreshExDate = new Date(System.currentTimeMillis()+REFRESH_AGE);
        Map<String, String> tokens = new HashMap<>();
        log.info("create token ... expiry//{}",expiryDate);
        Claims claims = Jwts.claims().setSubject(userId);//sub = email = userId
        claims.put("provider",provider);
        tokens.put("accessToken",
                Jwts.builder()
                .setClaims(claims)
                .setIssuer("pj07")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, ACCESS_SECRET_KEY)
                .compact());

        String refreshToken = Jwts.builder()
                        .setClaims(claims)
                        .setIssuer("pj07")
                        .setIssuedAt(new Date())
                        .setExpiration(refreshExDate)
                        .signWith(SignatureAlgorithm.HS512, REFRESH_SECRET_KEY)
                        .compact();
        tokens.put("refreshToken",refreshToken);
        userMapper.insertRefreshToken(userId, provider, refreshToken, TimeUtil.format(refreshExDate));

                log.info("tokens//{}",tokens);//ins 여기서 or 밖에서?
        return tokens;
    };

    // 엑세스토큰 재발행
    public String reissueAccessToken(String userId, Object roles){

        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("roles", roles); //롤 넣을까말까
        claims.put("provider", "google");
        Date now = new Date();
        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

        return Jwts.builder()
                .setClaims(claims)
//                .setSubject(userId)
                .setIssuer("pj07")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, ACCESS_SECRET_KEY)
                .compact();
    }

    //토큰에서 유저아이디(이메일) 가져오기
    public String getUsername(String token){
        return Jwts.parser()
                .setSigningKey(ACCESS_SECRET_KEY)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    //엑세스토큰 유효 아이디리턴
    public String validateAccessToken(String token){
        log.info("parsing access token sub");
        try {
            return Jwts.parser()
                    .setSigningKey(ACCESS_SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody().getSubject();
        }catch (ExpiredJwtException e){
            log.error("expired token");
            //-> validateRefreshToken--> reissueAccessToken

            throw e;
        }catch (Exception e){
            log.error("invalid accessToken // {}", e.getMessage());
            throw e;
        }
    }

    //리프래시토큰 유효성 파싱
    public String validateRefreshToken(String refreshToken){
        try {
            Claims claims = Jwts.parser().setSigningKey(REFRESH_SECRET_KEY)
                                         .parseClaimsJws(refreshToken)
                                         .getBody();
            //1.exp chk
            if (claims.getExpiration().before(new Date())) {
                log.info("claims expired");
                //리프레시 만료됨 = 다시 로그인
                return null;
            }
            //2.db chk
            Map<String, Object> fromDB = userMapper.selectRefreshToken(claims.getSubject());
            if(TimeUtil.isBeforeNow(fromDB.get("exp_date"))){
                log.info("db expired");
            }
            boolean dbMatchTf = refreshToken.equals(fromDB.get("refresh_token").toString());
            if(!dbMatchTf){
                log.info("db miss match");
                throw new Exception("invalid token");
            }

            //리프레시 토큰 남아있고, 디비체크완료 = accesstoken 재발급
            return reissueAccessToken(claims.get("sub").toString(), claims.get("roles"));
        }catch (Exception e) {
            return null;
        }
    }

    //인증정보 생성, 소셜도 이것 씀
    public Authentication getAuthentication(String token){
        AuthUserInfo userDetails = customUserService.loadUserByUsername(getUsername(token));
            return new UsernamePasswordAuthenticationToken(userDetails,
                                                 null,
                                                           userDetails.getAuthorities());
    }
    //DB 토큰 삭제
    public void invalidateRefreshToken(int userNo){
        userMapper.deleteRefreshToken(userNo);
    }


}
