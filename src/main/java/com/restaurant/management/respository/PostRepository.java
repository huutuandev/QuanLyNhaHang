package com.restaurant.management.respository;

import com.restaurant.management.models.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity,Long> {
    Page<PostEntity> findAllByIsDeletedFalse(Pageable pageable);
    Optional<PostEntity> findByIdAndIsDeletedFalse(Long id);

}
