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
                                new AntPathRequestMatcher(String.format("%s/auth/register", apiPrefix)),
                                new AntPathRequestMatcher(String.format("%s/auth/login", apiPrefix)),
                                new AntPathRequestMatcher(String.format("%s/categories",apiPrefix),"GET"),
                                new AntPathRequestMatcher(String.format("%s/posts",apiPrefix),"GET"),
                                new AntPathRequestMatcher(String.format("%s/posts/*",apiPrefix),"GET"),
                                new AntPathRequestMatcher(String.format("%s/categories/*",apiPrefix),"GET"),
                                new AntPathRequestMatcher(String.format("%s/home",apiPrefix),"GET"),
                                new AntPathRequestMatcher(String.format("%s/tables",apiPrefix),"GET"),
                                new AntPathRequestMatcher("/chat-websocket/**")
                        )
                        .permitAll()
                        .requestMatchers(
                                new AntPathRequestMatcher(String.format("%s/reservations", apiPrefix),"POST"),
                                new AntPathRequestMatcher(String.format("%s/reservations/*", apiPrefix)),
                                new AntPathRequestMatcher(String.format("%s/bills",apiPrefix)),
                                new AntPathRequestMatcher(String.format("%s/bills/**",apiPrefix)),
                                new AntPathRequestMatcher(String.format("%s/review/**",apiPrefix)),
                                new AntPathRequestMatcher(String.format("%s/users/profile", apiPrefix)),
                                new AntPathRequestMatcher(String.format("%s/users/change-password", apiPrefix)),
                                new AntPathRequestMatcher(String.format("%s/momo/**", apiPrefix),"POST")
                        ).hasAnyRole(RoleConstants.USER, RoleConstants.ADMIN)
                        .requestMatchers(
                                new AntPathRequestMatcher(String.format("%s/posts",apiPrefix),"POST"),
                                new AntPathRequestMatcher(String.format("%s/categories",apiPrefix),"POST"),
                                new AntPathRequestMatcher(String.format("%s/reservations", apiPrefix),"GET"),
                                new AntPathRequestMatcher(String.format("%s/reservations/{id}/status", apiPrefix),"POST"),
                                new AntPathRequestMatcher(String.format("%s/tables",apiPrefix),"POST"),
                                new AntPathRequestMatcher(String.format("%s/foods",apiPrefix)),
                                new AntPathRequestMatcher(String.format("%s/dashboard/**",apiPrefix)),
                                new AntPathRequestMatcher(String.format("%s/users/**", apiPrefix))
                        ).hasRole(RoleConstants.ADMIN)
                        .requestMatchers(
                                new AntPathRequestMatcher(String.format("%s/orders/**",apiPrefix))
                        ).hasRole(RoleConstants.STAFF)
                        .anyRequest().authenticated()
                );

        return http.build();
    }


}

