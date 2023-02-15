package com.example.pj0701.security.userInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class OAuth2Attributes implements OAuth2User {
    @Builder.Default private List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String provider;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;

    public static OAuth2Attributes of(String provider,
                                      String userNameAttributeName,
                                      Map<String, Object> attributes) {
        switch (provider){
            case "google":
                return ofGoogle(userNameAttributeName, attributes);
            case "kakao":
                return ofKaKao(userNameAttributeName, attributes);
            case "naver":
                return ofNaver(userNameAttributeName, attributes);
            default :
                return null;
        }
    }
    private static OAuth2Attributes ofGoogle(String userNameAttributeName,
                                             Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .email((String) attributes.get("email"))
                .firstName((String) attributes.get("given_name"))
                .lastName((String)attributes.get("family_name"))
                .provider("google")
                .attributes(attributes)
                .authorities(AuthorityUtils.createAuthorityList("ROLE_USER"))
                .nameAttributeKey(userNameAttributeName)
                .build();
    }//build Google OAuth2userInfo

    private static OAuth2Attributes ofKaKao(String userNameAttributeName,
                                            Map<String,Object> attributes){
        return null;
    }
    private static OAuth2Attributes ofNaver(String userNameAttributeName,
                                            Map<String,Object> attributes){
        return null;
    }

    @Override
    public Map<String, Object> getAttribute(String name) {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        return firstName+" "+lastName;
    }
    public String getProvider() {
        return provider;
    }
    public String getEmail() {
        return email;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
}
