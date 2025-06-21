package com.restaurant.management.service;

import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.DTO.UserLoginDTO;
import com.restaurant.management.customexceptions.PermissionDenyException;
import com.restaurant.management.models.UserEntity;

public interface IUserService {
    UserEntity createUser(UserDTO userDTO) throws Exception;
    String login(UserLoginDTO loginDTO) throws Exception;

}
