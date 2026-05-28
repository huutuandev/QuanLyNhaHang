package com.restaurant.management.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordRequest {
    @NotBlank(message = "Mật khẩu cũ không được trống")
    private String oldPassword;

    @NotBlank(message = "Mật khẩu mới không được trống")
    @Size(min = 6, message = "Mật khẩu mới phải ≥ 6 ký tự")
    private String newPassword;
}
