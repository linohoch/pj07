package com.example.pj0701.security.service;

import com.example.pj0701.proc.UserMapper;
import com.example.pj0701.security.userInfo.AuthUserInfo;
import com.example.pj0701.security.userInfo.OAuth2Attributes;
import com.example.pj0701.security.userInfo.Role;
import com.example.pj0701.vo.Pj07UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public AuthUserInfo loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String providerType = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        AuthUserInfo userInfo = AuthUserInfo.of(providerType,
                                                userNameAttributeName,
                                                oAuth2User.getAttributes());

        AuthUserInfo userDB = loadUserByUsername(oAuth2User.getAttribute("email"));
        int userNo = userDB.getUserNo();
        boolean firstVisit= userNo == 0 ;
        if (firstVisit) {
            Pj07UserInfoVO user = Pj07UserInfoVO.builder()
                    .userId(oAuth2User.getAttribute("email"))
                    .firstName(oAuth2User.getAttribute("firstName"))
                    .lastName(oAuth2User.getAttribute("lastName"))
                    .providerType(providerType)
                    .build();
            userNo = userMapper.createSocialUser(user);
        }
        userInfo.setUserNo(userNo);
        userInfo.setAuthorities(userDB.getAuthorities());

        if (userDB.getProvider() == null || !userDB.getProvider().equals(providerType)){
            //TODO
        }
        userMapper.updateLoginTimestamp(userNo);

        return userInfo;
        //-->OAuth2LoginAuthenticationProvider
    }

    //일반로그인
    @Override
    public AuthUserInfo loadUserByUsername(String userId) throws UsernameNotFoundException {

        Pj07UserInfoVO userInfoVO = Optional.ofNullable(userMapper.selectUser(userId))
                                            .orElseGet(Pj07UserInfoVO::new);
        Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();

//        if(userInfoVO.getAdminYn().equals("y")){
//            grantedAuthoritySet.add(new SimpleGrantedAuthority(Role.ADMIN.getCode()));
//        }
        grantedAuthoritySet.add(new SimpleGrantedAuthority(Role.MEMBER.getCode()));

        return AuthUserInfo.builder()
                .authorities(grantedAuthoritySet)
                .userNo(userInfoVO.getUserNo())
                .username(userInfoVO.getUserId())
                .password(userInfoVO.getPw())
                .role(Role.MEMBER)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();

        //--> provider
    }
}
