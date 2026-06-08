package com.restaurant.management.service;

import com.restaurant.management.dto.PostDTO;
import com.restaurant.management.dto.UserDTO;
import org.springframework.data.domain.Page;

public interface IPostService {
    Page<PostDTO> findAll(int page, int size);
    PostDTO findById(Long Id);
    PostDTO createOrUpdate(PostDTO postDTO, UserDTO userDTO);
    void deleteById(Long id);
}
