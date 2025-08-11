package com.restaurant.management.controller;

import com.restaurant.management.DTO.PostDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.responses.PagedResponse;
import com.restaurant.management.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final IPostService postService;

    @GetMapping
    public ResponseEntity<PagedResponse<PostDTO>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<PostDTO> postDTOPage = postService.findAll(page, size);
        return ResponseEntity.ok(new PagedResponse<>(postDTOPage, postDTOPage.getContent()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id){
        PostDTO postById = postService.findById(id);
        return ResponseEntity.ok(postById);
    }

    @PostMapping
    public ResponseEntity<?> creatOrUpdate(@Valid @RequestBody PostDTO postDTO, @AuthenticationPrincipal UserDTO userDTO){
            postService.createOrUpdate(postDTO,userDTO);
            return ResponseEntity.ok("Tạo thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        postService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
