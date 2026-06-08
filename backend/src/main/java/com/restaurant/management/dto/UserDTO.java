package com.restaurant.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    @JsonProperty("fullname")
    private String fullName;

//    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotBlank(message = "Password can not be blank")
    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;

    private String imageUrl;

    @JsonProperty("role_ids")
    @NotNull(message = "Role ids are required")
    private List<Long> roleIds;

    private List<String> roleNames;

    private String email;
}
