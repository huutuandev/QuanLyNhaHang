package com.restaurant.management.controller;

import com.restaurant.management.requests.ChangePasswordRequest;
import com.restaurant.management.dto.UserDTO;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Users", description = "Endpoints for managing user accounts and user profiles")
public class UserController {

    private final IUserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all registered users (Admin only).")
    public ResponseEntity<ApiResponse<List<UserDTO>>> findAll() {
        log.info("Fetching all registered users");
        List<UserDTO> users = userService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves details of a specific user by their ID.")
    public ResponseEntity<ApiResponse<UserDTO>> findById(@PathVariable Long id) {
        log.info("Fetching details for User ID: {}", id);
        UserDTO userDTO = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userDTO));
    }

    @PostMapping
    @Operation(summary = "Create or update user", description = "Saves or updates a user profile.")
    public ResponseEntity<ApiResponse<UserDTO>> createOrUpdate(@Valid @RequestBody UserDTO dto) {
        log.info("Creating/updating user profile: {}", dto.getPhoneNumber());
        UserDTO userDTO = userService.createOrUpdate(dto);
        return ResponseEntity.ok(ApiResponse.success("User saved successfully", userDTO));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Retrieves details of the currently authenticated user session.")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile(@AuthenticationPrincipal UserDTO user) {
        log.info("Fetching user profile for authenticated session user: {}", user.getPhoneNumber());
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", user));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change user password", description = "Allows current authenticated user to change their password after validating the old password.")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest dto,
                                                            @AuthenticationPrincipal UserDTO userDTO) {
        String phone = userDTO.getPhoneNumber();
        log.info("Request to change password for user phone: {}", phone);
        userService.changePassword(phone, dto);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", "Đổi mật khẩu thành công"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete user", description = "Soft-deletes a user account by its ID (Admin only).")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable Long id){
        log.info("Soft-deleting User ID: {}", id);
        userService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}
