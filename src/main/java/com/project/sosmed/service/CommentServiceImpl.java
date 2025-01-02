package com.project.sosmed.service;

import com.project.sosmed.entity.Comment;
import com.project.sosmed.entity.Post;
import com.project.sosmed.entity.User;
import com.project.sosmed.exception.BadRequestException;
import com.project.sosmed.exception.ResourceNotFoundException;
import com.project.sosmed.model.comment.CreateCommentRequest;
import com.project.sosmed.model.comment.CreateCommentResponse;
import com.project.sosmed.model.post.CommentRepliesResponse;
import com.project.sosmed.model.post.PostCommentsResponse;
import com.project.sosmed.repository.CommentRepository;
import com.project.sosmed.repository.PostRepository;
import com.project.sosmed.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public CreateCommentResponse createComment(CreateCommentRequest request) {
        String content = request.getContent();
        if (content == null || content.isEmpty()) {
            throw new BadRequestException("The content of the comment must not be null or empty");
        }

        UUID postId = UUID.fromString(request.getPostId());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post does not exist!"));

        UUID userId = UUID.fromString(request.getUserId());
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        Comment createComment = Comment.builder()
                .content(content)
                .post(post)
                .user(creator)
                .build();

        String parentCommentId = request.getParentCommentId();

        if (parentCommentId != null && !parentCommentId.isEmpty()) {
            UUID parentCommentUUID = UUID.fromString(parentCommentId);
            Comment parentComment = commentRepository.findById(parentCommentUUID)
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot reply to a non-existing comment"));

            createComment.setParentComment(parentComment);
        }

        Comment createdComment = commentRepository.save(createComment);

        Comment currParentComment = createdComment.getParentComment();

        return CreateCommentResponse.builder()
                .id(createdComment.getId())
                .content(createdComment.getContent())
                .createdDate(createdComment.getCreatedDate())
                .postId(createdComment.getPost().getId())
                .parentCommentId(currParentComment != null ? currParentComment.getId() : null)
                .commenterId(createdComment.getUser().getId())
                .commenter(createComment.getUser().getUsername())
                .build();
    }

    @Override
    public Page<PostCommentsResponse> getCommentsFromPost(String postId, int page, int size) {
        UUID postUUID = UUID.fromString(postId);

        if (!postRepository.existsById(postUUID)) {
            throw new ResourceNotFoundException("Post not found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        return commentRepository.findByPostId(postUUID, pageable);
    }

    @Override
    public List<CommentRepliesResponse> getCommentReplies(String parentCommentId) {
        UUID parentCommentUUID = UUID.fromString(parentCommentId);

        if (!commentRepository.existsById(parentCommentUUID)) {
            throw new ResourceNotFoundException("Parent comment not found");
        }

        return commentRepository.findByParentCommentId(parentCommentUUID);
    }

    @Override
    public void deleteComment(String commentId) {
        UUID commentUUID = UUID.fromString(commentId);

        if (!commentRepository.existsById(commentUUID)) {
            throw new ResourceNotFoundException("Comment does not exist in the database!");
        }

        commentRepository.deleteById(commentUUID);
    }
}
