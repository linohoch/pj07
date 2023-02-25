package com.example.pj0701.security.userInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AuthUserInfo implements UserDetails, OAuth2User, OidcUser, CredentialsContainer {
    private Set<GrantedAuthority> authorities;
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String username;
    private String password;

    private String provider;
    private String email;
    private String firstName;
    private String lastName;

    private Role role;
    private int userNo;

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public static AuthUserInfo of(String provider,
                                  String userNameAttributeName,
                                  Map<String, Object> attributes) {
        switch (provider){
            case "google":
                return ofGoogle(userNameAttributeName, attributes);
            default :
                return null;
        }
    }
    private static AuthUserInfo ofGoogle(String userNameAttributeName,
                                             Map<String, Object> attributes) {

        return AuthUserInfo.builder()
                .username((String) attributes.get("email"))
                .email((String) attributes.get("email"))
                .firstName((String) attributes.get("given_name"))
                .lastName((String) attributes.get("family_name"))
                .provider("google")
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .role(Role.MEMBER)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }



    public String getAttributes(String name) {
        return this.attributes.get(name).toString();
    }
    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public Map<String, Object> getClaims() {
        return null;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }

    @Override
    public void eraseCredentials() {

    }
}
