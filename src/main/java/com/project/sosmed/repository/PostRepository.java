package com.project.sosmed.repository;

import com.project.sosmed.entity.Post;
import com.project.sosmed.model.post.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("SELECT new com.project.sosmed.model.post.PostResponse(" +
            "p.id, " +
            "p.body, " +
            "p.user.id, " +
            "p.user.username, " +
            "p.createdDate, " +
            "p.updatedDate, " +
            "(SELECT COUNT(c.id) FROM Comment c WHERE c.post = p), " +
            "(SELECT COUNT(l.id) FROM Like l WHERE l.post = p)) " +
            "FROM Post p " +
            "ORDER BY p.createdDate DESC")
    Page<PostResponse> getAllPostsWithTotalLikesAndComments(Pageable pageable);

    @Query("SELECT new com.project.sosmed.model.post.PostResponse(" +
            "p.id, " +
            "p.body, " +
            "p.user.id, " +
            "p.user.username, " +
            "p.createdDate, " +
            "p.updatedDate, " +
            "(SELECT COUNT(c.id) FROM Comment c WHERE c.post = p), " +
            "(SELECT COUNT(l.id) FROM Like l WHERE l.post = p)) " +
            "FROM Post p " +
            "JOIN Follow f ON f.followedUser = p.user " +
            "WHERE f.followingUser.id = :userId " +
            "ORDER BY p.createdDate DESC")
    Page<PostResponse> getFollowedUserPostsWithTotalLikesAndComments(UUID userId, Pageable pageable);

}
