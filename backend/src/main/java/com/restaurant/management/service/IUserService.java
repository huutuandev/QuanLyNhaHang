package com.restaurant.management.service;

import com.restaurant.management.requests.ChangePasswordRequest;
import com.restaurant.management.dto.UserDTO;
import com.restaurant.management.models.UserEntity;
import com.restaurant.management.requests.LoginRequest;
import com.restaurant.management.requests.RegisterRequest;

import java.util.List;

public interface IUserService {
    UserEntity register(RegisterRequest registerRequest) throws Exception;
    String login(LoginRequest loginRequest) throws Exception;
    List<UserDTO> findAll();
    UserDTO findById(Long id);
    UserDTO createOrUpdate(UserDTO userDTO);
    void deleteById(Long Id);
    void changePassword(String phone, ChangePasswordRequest dto);

}
