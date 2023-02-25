package com.example.pj0701.security.config;

import com.example.pj0701.security.handler.*;
import com.example.pj0701.security.service.CustomUserService;
import com.example.pj0701.security.jwt.JwtAuthenticationFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = false)
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
    private final CustomLogoutHandler customLogoutHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

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
                .authorizeRequests()
                .antMatchers("/","/auth/**", "/auth2/**", "/swagger-ui/**","/swagger/**","/board/list").permitAll()//접근허용경로
                .antMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated()

//            .and()
//                .formLogin()
//                .loginPage("/auth/loginForm")
//                .usernameParameter("userId")
//                .passwordParameter("pw")
//                .loginProcessingUrl("/auth2/login") //액션url
//                .failureUrl("/auth/loginForm")
//                .successHandler(basicSuccessHandler)

//                .successHandler((request, response, authentication) -> response.sendRedirect("/"))
//                .failureHandler((request, response, exception) -> {
//                    request.getSession().setAttribute("error.message", exception.getMessage());
//                    response.sendRedirect("/login");
//                })
//            .and()
//                .logout()
//                .logoutUrl("/auth/logout") //액션url
//                .addLogoutHandler(customLogoutHandler)
//                .logoutSuccessUrl("/")

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
                .defaultSuccessUrl("/",false)
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)
            .and()
                .logout()
                .logoutUrl("/auth2/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .addLogoutHandler(customLogoutHandler)
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler);
        // Filter
        http    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//                .addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);

        return http.build();
    }
}
