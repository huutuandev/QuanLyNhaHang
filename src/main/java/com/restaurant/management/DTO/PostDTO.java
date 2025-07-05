    package com.restaurant.management.DTO;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public class PostDTO {
        private Integer Id;
        private String Title;
        private String Content;
        private String ImagUrl;
        private String AuthorName;
    }
