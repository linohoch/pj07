package com.example.pj0701.security.service;

import com.example.pj0701.proc.UserMapper;
import com.example.pj0701.security.userInfo.AuthUserInfo;
import com.example.pj0701.security.userInfo.OAuth2Attributes;
import com.example.pj0701.security.userInfo.Role;
import com.example.pj0701.vo.Pj07UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>,
                                          UserDetailsService {
    final UserMapper userMapper;

    //소셜로그인
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        log.info("loadUser in CustomUserService");

        String provider = userRequest
                .getClientRegistration()
                .getRegistrationId();
        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        log.info("oAuth2User // attributes -> {} // authorities -> {}", oAuth2User.getAttributes(), oAuth2User.getAuthorities());

//--------
//        Pj07UserInfoVO DBUser = Optional.ofNullable(
//                                    userMapper.selectUserById(
//                                            oAuth2User.getAttributes().get("email").toString()
//                                    )
//                                ).orElseGet(Pj07UserInfoVO::new);
//
//        log.info("is null? // {}", DBUser);//userNo int로 해놔서 0넘어옴.
//
//            //DB 회원이 아닌 경우
//            if (DBUser.getUserNo() == 0) {
//                log.info("not signed email // {}", oAuth2User.getAttributes().get("email"));
//                //소셜 회원등록 진행
//                Pj07UserInfoVO pj07UserInfoVO= Pj07UserInfoVO.builder()
//                        .userId(oAuth2User.getAttributes().get("email").toString())
//                        .firstName(oAuth2User.getAttributes().get("given_name").toString())
//                        .lastName(oAuth2User.getAttributes().get("family_name").toString())
//                        .providerType(provider)
//                        .build();
//                userMapper.createSocialUser(pj07UserInfoVO);
//            }
//            //DB 회원이지만 로그인방식이 다른 경우
//            if (DBUser.getProviderType() == null || !DBUser.getProviderType().equals(provider)) {
//                log.info("miss matched signup type");
//                //로그인 방식을 추가할지
//            }
//            userMapper.updateLoginTimestamp(DBUser.getUserNo());
//-----이상의 로직을 success핸들러로 이동

        return OAuth2Attributes.of(provider,
                                   userNameAttributeName,
                                   oAuth2User.getAttributes());
        //-->OAuth2LoginAuthenticationProvider
    }

    //일반로그인
    @Override
    public AuthUserInfo loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("loadUserByUsername in CustomUserService");
        Pj07UserInfoVO userInfoVO = new Pj07UserInfoVO();
        if(userMapper.countUserById(userId)>0)
            userInfoVO = userMapper.selectUser(userId);

        Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
//        if(userInfoVO.getAdminYn .equals("y")){
//        grantedAuthoritySet.add(new SimpleGrantedAuthority(Role.ADMIN.getCode()));
//          }
        grantedAuthoritySet.add(new SimpleGrantedAuthority(Role.MEMBER.getCode()));
//        return new User(userInfoVO.getUserId(),
//                        userInfoVO.getPw(),
//                        grantedAuthoritySet);
        return AuthUserInfo.builder()
                .userNo(userInfoVO.getUserNo())
                .username(userInfoVO.getUserId())
                .password(userInfoVO.getPw())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .authorities(grantedAuthoritySet)
                .build();

        //--> provider
    }
}
