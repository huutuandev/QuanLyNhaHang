package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.DTO.UserLoginDTO;
import com.restaurant.management.components.JwtTokenUtil;
import com.restaurant.management.constant.RoleConstants;
import com.restaurant.management.customexceptions.DataNotFoundException;
import com.restaurant.management.customexceptions.PermissionDenyException;
import com.restaurant.management.customexceptions.ResourceNotFoundException;
import com.restaurant.management.models.RoleEntity;
import com.restaurant.management.models.UserEntity;
import com.restaurant.management.respository.RoleRepository;
import com.restaurant.management.respository.UserRepository;
import com.restaurant.management.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    private final AuthenticationManager authenticationManager;

    @Override
    public UserEntity createUser(UserDTO userDTO) throws Exception {
        if(userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }

        List<RoleEntity> roles = new ArrayList<>();
        for (Long roleId : userDTO.getRoleIds()) {
            RoleEntity role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new DataNotFoundException("Role not found with id: " + roleId));
            if (RoleConstants.ADMIN.equalsIgnoreCase(role.getRoleName())) {
                throw new PermissionDenyException("You cannot register an admin account");
            }
            roles.add(role);
        }

        UserEntity newUser = UserEntity.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail())
                .roles(roles)
                .build();
        return userRepository.save(newUser);
    }

    @Override
    public String login(UserLoginDTO loginDTO) throws Exception {
        String phoneNumber = loginDTO.getPhoneNumber();
        String password = loginDTO.getPassword();

        Optional<UserEntity> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (!optionalUser.isPresent()) {
            throw new DataNotFoundException("Invalid phone number or password");
        }
        UserEntity user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Wrong phone number or password");
        }
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(phoneNumber, password, user.getAuthorities());
        System.out.println("Role là"+user.getAuthorities());
        authenticationManager.authenticate(authToken);
        return jwtTokenUtil.generateToken(user);
    }
}
