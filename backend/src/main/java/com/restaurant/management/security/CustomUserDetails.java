package com.restaurant.management.security;

import com.restaurant.management.models.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetails build(UserEntity user) {
        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getPhoneNumber())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .authorities(user.getAuthorities())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
