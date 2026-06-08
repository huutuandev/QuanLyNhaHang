package com.restaurant.management.controller;

import com.restaurant.management.dto.PostDTO;
import com.restaurant.management.dto.UserDTO;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.PagedResponse;
import com.restaurant.management.service.IPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Posts", description = "Endpoints for managing restaurant blog posts and news articles")
public class PostController {

    private final IPostService postService;

    @GetMapping
    @Operation(summary = "Get all posts", description = "Retrieves a paginated list of blog posts and news articles.")
    public ResponseEntity<ApiResponse<PagedResponse<PostDTO>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        log.info("Fetching blog posts: Page: {}, Size: {}", page, size);
        Page<PostDTO> postDTOPage = postService.findAll(page, size);
        PagedResponse<PostDTO> pagedResponse = new PagedResponse<>(postDTOPage, postDTOPage.getContent());
        return ResponseEntity.ok(ApiResponse.success("Blog posts retrieved successfully", pagedResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get post by ID", description = "Retrieves the details of a specific blog post by its ID.")
    public ResponseEntity<ApiResponse<PostDTO>> getPostById(@PathVariable Long id){
        log.info("Fetching blog post with ID: {}", id);
        PostDTO postById = postService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Blog post retrieved successfully", postById));
    }

    @PostMapping
    @Operation(summary = "Create or update post", description = "Creates a new blog post or updates an existing one.")
    public ResponseEntity<ApiResponse<String>> creatOrUpdate(@Valid @RequestBody PostDTO postDTO, @AuthenticationPrincipal UserDTO userDTO){
        log.info("Creating/updating blog post: {} by User ID: {}", postDTO.getTitle(), userDTO.getId());
        postService.createOrUpdate(postDTO, userDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo thành công", "Tạo thành công"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete post", description = "Deletes a blog post by its ID.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id){
        log.info("Deleting blog post with ID: {}", id);
        postService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Blog post deleted successfully", null));
    }
}
