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
