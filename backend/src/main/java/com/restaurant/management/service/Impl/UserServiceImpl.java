package com.restaurant.management.service.Impl;

import com.restaurant.management.requests.ChangePasswordRequest;
import com.restaurant.management.dto.UserDTO;
import com.restaurant.management.components.JwtTokenUtil;
import com.restaurant.management.constant.RoleConstants;
import com.restaurant.management.customexceptions.DataNotFoundException;
import com.restaurant.management.models.RoleEntity;
import com.restaurant.management.models.UserEntity;
import com.restaurant.management.requests.RegisterRequest;
import com.restaurant.management.respository.RoleRepository;
import com.restaurant.management.respository.UserRepository;
import com.restaurant.management.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtTokenUtil jwtTokenUtil;

    private final AuthenticationManager authenticationManager;

    @Override
    public UserEntity register(RegisterRequest registerRequest) throws Exception {
        String phone = registerRequest.getPhone();
        if(userRepository.existsByPhoneNumber(phone)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }

        RoleEntity customerRole = roleRepository.findByRoleName(RoleConstants.USER)
                .orElseThrow(() -> new DataNotFoundException("Default role CUSTOMER not found"));

        List<RoleEntity> roles = new ArrayList<>();
        roles.add(customerRole);

        UserEntity newUser = UserEntity.builder()
                .fullName(registerRequest.getUsername())
                .phoneNumber(phone)
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .roles(roles)
                .build();
        return userRepository.save(newUser);
    }

    @Override
    public String login(com.restaurant.management.requests.LoginRequest loginRequest) throws Exception {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        Optional<UserEntity> optionalUser = userRepository.findByPhoneNumber(username);
        if (!optionalUser.isPresent()) {
            throw new DataNotFoundException("Invalid phone number or password");
        }
        UserEntity user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Wrong phone number or password");
        }
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
        log.info("Role là: {}", user.getAuthorities());
        authenticationManager.authenticate(authToken);
        return jwtTokenUtil.generateToken(user);
    }

    @Override
    public List<UserDTO> findAll() {
        List<UserEntity> userEntities = userRepository.findAllByIsDeletedFalse();
        return userEntities.stream().map(user ->
                UserDTO.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .phoneNumber(user.getPhoneNumber())
                        .email(user.getEmail())
                        .imageUrl(user.getImageUrl())
                        .roleIds(
                                user.getRoles()
                                        .stream()
                                        .map(RoleEntity::getId)
                                        .collect(Collectors.toList())
                        )
                        .roleNames(user.getAuthorities()
                                .stream().map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList())
                        )
                        .build()
        ).collect(Collectors.toList());
    }
    @Override
    public UserDTO findById(Long id) {
        UserEntity user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Lỗi không phân định"));
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .roleIds(
                        user.getRoles()
                                .stream()
                                .map(RoleEntity::getId)
                                .collect(Collectors.toList())
                )
                .roleNames(user.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
                )
                .build();
    }

    @Override
    public UserDTO createOrUpdate(UserDTO userDTO) {
        UserEntity user;
        if(userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        if (userDTO.getId() != null) {
            user = userRepository.findByIdAndIsDeletedFalse(userDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            user.setFullName(userDTO.getFullName());
            user.setEmail(userDTO.getEmail());
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setImageUrl(userDTO.getImageUrl());
        } else {
            user = modelMapper.map(userDTO, UserEntity.class);
            user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }
        if (userDTO.getRoleIds() != null) {
            List<RoleEntity> roles = roleRepository.findAllById(userDTO.getRoleIds());
            user.setRoles(roles);
        }
        UserEntity saved = userRepository.save(user);
        return modelMapper.map(saved, UserDTO.class);
    }


    @Override
    public void deleteById(Long id) {
        UserEntity user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Lỗi không phân định"));
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Override
    public void changePassword(String phone, ChangePasswordRequest dto) {
        UserEntity user = userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác");
        }
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

}
