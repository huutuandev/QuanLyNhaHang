package com.restaurant.management.service;

import com.restaurant.management.requests.ChangePasswordRequest;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.DTO.UserLoginDTO;
import com.restaurant.management.models.UserEntity;

import java.util.List;

public interface IUserService {
    UserEntity createUser(UserDTO userDTO) throws Exception;
    String login(UserLoginDTO loginDTO) throws Exception;
    List<UserDTO> findAll();
    UserDTO findById(Long id);
    UserDTO createOrUpdate(UserDTO userDTO);
    void deleteById(Long Id);
    void changePassword(String phone, ChangePasswordRequest dto);

}
