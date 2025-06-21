package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.PostDTO;
import com.restaurant.management.customexceptions.ResourceNotFoundException;
import com.restaurant.management.models.PostEntity;
import com.restaurant.management.respository.PostRepository;
import com.restaurant.management.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements IPostService {

    private final PostRepository postRepository;

    @Override
    public List<PostDTO>  findAll() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC,"createdAt"))
                .stream().map(postEntity ->
                PostDTO.builder()
                        .Id(postEntity.getId())
                        .Title(postEntity.getTitle())
                        .Content(postEntity.getContent())
                        .ImagUrl(postEntity.getImageUrl())
                        .AuthorName(postEntity.getAuthor().getFullName())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public PostDTO findById(Integer Id) {
        PostEntity postEntity = postRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return PostDTO.builder()
                .Id(postEntity.getId())
                .Title(postEntity.getTitle())
                .Content(postEntity.getContent())
                .ImagUrl(postEntity.getImageUrl())
                .AuthorName(postEntity.getAuthor().getFullName())
                .build();
    }

}
