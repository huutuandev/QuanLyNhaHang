package com.restaurant.management.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data //toString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Password can not be blank")
    private String password;
}
