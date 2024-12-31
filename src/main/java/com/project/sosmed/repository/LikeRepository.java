package com.project.sosmed.repository;

import com.project.sosmed.entity.Like;
import com.project.sosmed.entity.Post;
import com.project.sosmed.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {
    Optional<Like> findByUserAndPost(User user, Post post);

    @Query("SELECT l.post.id FROM Like l WHERE l.user.id = :userId AND l.post.id IN :postIds")
    List<UUID> findLikedPostIdByUserIdAndPostId(@Param("userId") UUID userId, @Param("postIds") List<UUID> postIds);
}
