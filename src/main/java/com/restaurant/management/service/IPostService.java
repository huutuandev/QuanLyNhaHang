package com.restaurant.management.service;

import com.restaurant.management.DTO.PostDTO;
import com.restaurant.management.DTO.UserDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IPostService {
    Page<PostDTO> findAll(int page, int size);
    PostDTO findById(Long Id);
    PostDTO createOrUpdate(PostDTO postDTO, UserDTO userDTO);
    void deleteById(Long id);
}
