    package com.restaurant.management.DTO;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public class PostDTO {
        private Long id;
        private String title;
        private String content;
        private String imageUrl;
        private String authorName;
    }
