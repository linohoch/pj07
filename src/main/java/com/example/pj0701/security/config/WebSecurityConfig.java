package com.example.pj0701.security.config;

import com.example.pj0701.security.handler.BasicSuccessHandler;
import com.example.pj0701.security.handler.OAuth2FailureHandler;
import com.example.pj0701.security.handler.OAuth2SuccessHandler;
import com.example.pj0701.security.service.BasicAuthenticationProvider;
import com.example.pj0701.security.service.CustomUserService;
import com.example.pj0701.security.jwt.JwtAuthenticationFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true, //어노테이션으로 퍼밋하려면
        securedEnabled = true,
        jsr250Enabled = true
)
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserService customUserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final BasicSuccessHandler basicSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .antMatchers("/ignore1", "/ignore2");
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
            corsConfiguration.setAllowedMethods(Arrays.asList("GET","POST"));
    //        corsConfiguration.setMaxAge();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**",corsConfiguration);
        return source;
    }
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .cors()
            .and()
                .csrf().disable()
                .httpBasic().disable()
//                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
                .authorizeRequests().antMatchers("/admin").hasRole("ADMIN")
                                    .antMatchers("**","/auth/**", "/auth2/**").permitAll()//접근허용경로
                .anyRequest().authenticated()

            .and()
                .formLogin()
                .loginPage("/auth/loginForm")
                .usernameParameter("userId")
                .passwordParameter("pw")
                .loginProcessingUrl("/auth2/login") //액션url
                .defaultSuccessUrl("/",false)
                .failureUrl("/auth/loginForm")
                .successHandler(basicSuccessHandler)
//                .successHandler((request, response, authentication) -> response.sendRedirect("/"))
//                .failureHandler((request, response, exception) -> {
//                    request.getSession().setAttribute("error.message", exception.getMessage());
//                    response.sendRedirect("/login");
//                })
            .and()
                .logout()
                .logoutUrl("/logout") //액션url
                .logoutSuccessUrl("/")


        // OAuth2
            .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
            .and()
                .redirectionEndpoint()
                .baseUri("/login/oauth2/code/*")
            .and()
                .userInfoEndpoint()
                .userService(customUserService)
            .and()
                .defaultSuccessUrl("/",true)
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler);

        // Filter
        http    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//                .addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);

        return http.build();
    }
}
