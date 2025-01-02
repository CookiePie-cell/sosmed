package com.project.sosmed.repository;

import com.project.sosmed.entity.Comment;
import com.project.sosmed.entity.Post;
import com.project.sosmed.model.post.CommentRepliesResponse;
import com.project.sosmed.model.post.PostCommentsResponse;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("SELECT new com.project.sosmed.model.post.PostCommentsResponse(" +
            "c.id, " +
            "c.content, " +
            "c.createdDate, " +
            "c.deletedDate, " +
            "c.parentComment.id, " +
            "c.post.id, " +
            "c.user.id, " +
            "c.user.username, " +
            "(SELECT COUNT(l.id) FROM Like l WHERE l.comment = c), " +
            "(SELECT COUNT(r.id) FROM Comment r WHERE r.parentComment = c))" +
            "FROM Comment c " +
            "WHERE c.post.id = :postId AND c.parentComment IS NULL " +
            "ORDER BY c.createdDate DESC")
    Page<PostCommentsResponse> findByPostId(@Param("postId") UUID postId, Pageable pageable);

    @Query("SELECT new com.project.sosmed.model.post.CommentRepliesResponse(" +
            "c.id, " +
            "c.content, " +
            "c.createdDate, " +
            "c.deletedDate, " +
            "new com.project.sosmed.model.post.OriginalCommentData(" +
                "c.parentComment.id, " +
                "c.parentComment.user.id, " +
                "c.parentComment.user.username)," +
            "c.post.id, " +
            "c.user.id, " +
            "c.user.username, " +
            "(SELECT COUNT(l.id) FROM Like l WHERE l.comment = c)) " +
            "FROM Comment c " +
            "WHERE c.parentComment IS NOT NULL AND c.parentComment.id = :parentCommentId " +
            "ORDER BY c.createdDate DESC")
    List<CommentRepliesResponse> findByParentCommentId(@Param("parentCommentId") UUID parentCommentId);
}

