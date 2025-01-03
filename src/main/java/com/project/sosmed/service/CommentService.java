package com.project.sosmed.service;

import com.project.sosmed.model.comment.CreateCommentRequest;
import com.project.sosmed.model.comment.CreateCommentResponse;
import com.project.sosmed.model.post.CommentRepliesResponse;
import com.project.sosmed.model.post.PostCommentsResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {
    CreateCommentResponse createComment(CreateCommentRequest request);

    Page<PostCommentsResponse> getCommentsFromPost(String postId, int page, int size);

    List<CommentRepliesResponse> getCommentReplies(String parentCommentId);

    void deleteComment(String commentId, String userId);
}
