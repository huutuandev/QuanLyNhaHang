package com.restaurant.management.controller;

import com.restaurant.management.components.JwtTokenUtil;
import com.restaurant.management.models.RefreshTokenEntity;
import com.restaurant.management.models.UserEntity;
import com.restaurant.management.requests.LoginRequest;
import com.restaurant.management.requests.RegisterRequest;
import com.restaurant.management.requests.TokenRefreshRequest;
import com.restaurant.management.responses.*;
import com.restaurant.management.service.IRefreshTokenService;
import com.restaurant.management.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication", description = "Endpoints for user registration, login, logout, and token refresh")
public class AuthController {
    private final IUserService userService;
    private final UserDetailsService userDetailsService;
    private final IRefreshTokenService refreshTokenService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user profile. Admin role registration is prohibited through this endpoint.")
    public ResponseEntity<ApiResponse<RegisterResponse>> registerUser(@Valid @RequestBody RegisterRequest request) {
        log.info("Request to register user with phone number: {}", request.getPhone());
        try {
            UserEntity user = userService.register(request);
            log.info("User registered successfully: {}", request.getPhone());
            String roleName = user.getRoles().isEmpty() ? "" : user.getRoles().get(0).getRoleName();
            RegisterResponse response = RegisterResponse.builder()
                    .id(user.getId())
                    .username(user.getFullName())
                    .email(user.getEmail())
                    .phone(user.getPhoneNumber())
                    .role(roleName)
                    .build();
            return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
        } catch (Exception e) {
            log.error("Failed to register user: {}", e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates user and returns JWT access token along with a refresh token.")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@Valid @RequestBody LoginRequest request) {
        log.info("User login attempt with phone number: {}", request.getUsername());
        try {
            String accessToken = userService.login(request);
            UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(request.getUsername());
            RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user.getId());

            String roleName = user.getRoles().isEmpty() ? "" : user.getRoles().get(0).getRoleName();
            UserInfoResponse userInfo = UserInfoResponse.builder()
                    .id(user.getId())
                    .username(user.getFullName())
                    .email(user.getEmail())
                    .phone(user.getPhoneNumber())
                    .role(roleName)
                    .build();

            LoginResponse loginResponse = LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .user(userInfo)
                    .build();

            log.info("User logged in successfully: {}", request.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
        } catch (Exception e) {
            log.error("Login failed for phone number {}: {}", request.getUsername(), e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT Access Token", description = "Uses a valid, non-expired refresh token to generate a new JWT access token.")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        log.info("Token refresh request received");
        String requestRefreshToken = request.getRefreshToken();

        TokenRefreshResponse response = refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUser)
                .map(user -> {
                    try {
                        String token = jwtTokenUtil.generateToken(user);
                        log.info("New access token generated successfully for user: {}", user.getPhoneNumber());
                        return new TokenRefreshResponse(token, requestRefreshToken);
                    } catch (Exception e) {
                        log.error("Error generating token for user during refresh: {}", e.getMessage());
                        throw new IllegalStateException("Error generating access token: " + e.getMessage());
                    }
                })
                .orElseThrow(() -> {
                    log.warn("Invalid refresh token attempt: {}", requestRefreshToken);
                    return new IllegalArgumentException("Refresh token is invalid or does not exist!");
                });

        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Invalidates the user session by revoking the refresh token in the database.")
    public ResponseEntity<ApiResponse<String>> logoutUser(@Valid @RequestBody TokenRefreshRequest request) {
        log.info("Logout request received");
        refreshTokenService.revokeToken(request.getRefreshToken());
        log.info("Refresh token revoked successfully");
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", "OK"));
    }
}
