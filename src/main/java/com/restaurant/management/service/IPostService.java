package com.restaurant.management.service;

import com.restaurant.management.DTO.PostDTO;

import java.util.List;

public interface IPostService {
    List<PostDTO> findAll(int page, int size);
    PostDTO findById(Integer Id);
}
