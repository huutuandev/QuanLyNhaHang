package com.restaurant.management.filters;

import com.restaurant.management.dto.UserDTO;
import com.restaurant.management.components.JwtTokenUtil;
import com.restaurant.management.models.UserEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {


    @Value("${api.prefix}")
    private String apiPrefix;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String servletPath = request.getServletPath();
            log.info("Request path: {}, Method: {}", servletPath, request.getMethod());
            if (isBypassToken(request)) {
                log.info("Bypassing token for: {}", servletPath);
                filterChain.doFilter(request, response);
                return;
            }
            if (request.getServletPath().startsWith("/chat-websocket")) {
                filterChain.doFilter(request, response);
                return;
            }
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: No token");
                return;
            }
                        String token = authHeader.substring(7);
            String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
            if (phoneNumber != null) {
                Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

                boolean needSetAuth = false;

                if (currentAuth == null) {
                    needSetAuth = true;
                } else if (currentAuth.getPrincipal() instanceof com.restaurant.management.security.CustomUserDetails) {
                    String currentPhone = ((com.restaurant.management.security.CustomUserDetails) currentAuth.getPrincipal()).getPhoneNumber();
                    if (!phoneNumber.equals(currentPhone)) {
                        needSetAuth = true;
                    }
                } else {
                    needSetAuth = true;
                }

                if (needSetAuth) {
                    UserEntity userEntity = (UserEntity) userDetailsService.loadUserByUsername(phoneNumber);
                    if (jwtTokenUtil.validateToken(token, userEntity)) {
                        com.restaurant.management.security.CustomUserDetails customUserDetails = com.restaurant.management.security.CustomUserDetails.build(userEntity);
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                        return;
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + e.getMessage());
        }
    }

    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/foods/*", apiPrefix), "GET"),
                Pair.of(String.format("%s/auth/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/refresh", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/logout", apiPrefix), "POST"),
                Pair.of(String.format("%s/categories", apiPrefix), "GET"),
                Pair.of(String.format("%s/posts", apiPrefix), "GET"),
                Pair.of(String.format("%s/posts/*", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/home", apiPrefix), "GET"),
                Pair.of(String.format("%s/payment/vnpay/ipn", apiPrefix), "POST"),
                Pair.of(String.format("%s/payment/momo/ipn", apiPrefix), "POST"),
                Pair.of("/swagger-ui", "GET"),
                Pair.of("/v3/api-docs", "GET"),
                Pair.of("/swagger-ui.html", "GET")
        );
        for (Pair<String, String> bypassToken : bypassTokens) {
            if (request.getServletPath().contains(bypassToken.getFirst()) &&
                    request.getMethod().equals(bypassToken.getSecond())) {
                return true;
            }
        }
        return false;
    }
}