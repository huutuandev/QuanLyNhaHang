package com.restaurant.management.configurations;

import com.restaurant.management.constant.RoleConstants;
import com.restaurant.management.filters.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Value("${api.prefix}")
    private String apiPrefix;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher(String.format("%s/users/register", apiPrefix)),
                                new AntPathRequestMatcher(String.format("%s/users/login", apiPrefix)),
                                new AntPathRequestMatcher(String.format("%s/categories",apiPrefix),"GET"),
                                new AntPathRequestMatcher(String.format("%s/posts",apiPrefix),"GET"),
                                new AntPathRequestMatcher(String.format("%s/posts/*",apiPrefix),"GET"),
                                new AntPathRequestMatcher(String.format("%s/categories/**",apiPrefix),"GET"),
                                new AntPathRequestMatcher(String.format("%s/home",apiPrefix),"GET")
                        )
                        .permitAll()
                        .requestMatchers(
                                new AntPathRequestMatcher(String.format("%s/users/me", apiPrefix), "GET")
                        ).hasAnyRole(RoleConstants.USER)
                        .anyRequest().authenticated()
                );

        return http.build();
    }


}

