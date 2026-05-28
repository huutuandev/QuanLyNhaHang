package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.PostDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.customexceptions.ResourceNotFoundException;
import com.restaurant.management.models.PostEntity;
import com.restaurant.management.models.UserEntity;
import com.restaurant.management.respository.PostRepository;
import com.restaurant.management.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements IPostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<PostDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostEntity> postPage = postRepository.findAllByIsDeletedFalse(pageable);
        return postPage.map(post -> modelMapper.map(post, PostDTO.class));
    }



    @Override
    public PostDTO findById(Long Id) {
        PostEntity postEntity = postRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return modelMapper.map(postEntity,PostDTO.class);
    }

    @Override
    public PostDTO createOrUpdate(PostDTO postDTO, UserDTO userDTO) {
        PostEntity post;
        if(postDTO.getId() != null){
            post = postRepository.findById(postDTO.getId())
                    .orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy"));
            modelMapper.map(postDTO,post);
            post.setUpdatedAt(LocalDateTime.now());
        }
        else {
            post = modelMapper.map(postDTO, PostEntity.class);
        }
        UserEntity author = new UserEntity();
        author.setId(userDTO.getId());
        post.setAuthor(author);
        PostEntity saved = postRepository.save(post);
        return modelMapper.map(saved,PostDTO.class);
    }

    @Override
    public void deleteById(Long id) {
        PostEntity postEntity = postRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy"));
        postEntity.setIsDeleted(true);
        postRepository.save(postEntity);
    }

}
