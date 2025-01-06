package com.project.sosmed.service;

import com.project.sosmed.entity.Comment;
import com.project.sosmed.entity.Like;
import com.project.sosmed.entity.Post;
import com.project.sosmed.entity.User;
import com.project.sosmed.exception.BadRequestException;
import com.project.sosmed.exception.ResourceNotFoundException;
import com.project.sosmed.model.comment.CommentLikeRequest;
import com.project.sosmed.model.comment.CommentLikeResponse;
import com.project.sosmed.model.comment.CreateCommentRequest;
import com.project.sosmed.model.comment.CreateCommentResponse;
import com.project.sosmed.model.post.CommentRepliesResponse;
import com.project.sosmed.model.post.PostCommentsResponse;
import com.project.sosmed.repository.CommentRepository;
import com.project.sosmed.repository.LikeRepository;
import com.project.sosmed.repository.PostRepository;
import com.project.sosmed.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

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
    public CommentLikeResponse LikeComment(CommentLikeRequest request) {
        UUID userId = UUID.fromString(request.getUserId());
        UUID postId = UUID.fromString(request.getCommentId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = commentRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        Optional<Like> like = likeRepository.findByUserAndComment(user, comment);

        if (like.isPresent()) {
            likeRepository.delete(like.get());
            return CommentLikeResponse.builder().message("Comment unliked").build();
        }

        Like createLike = Like.builder()
                .user(user)
                .comment(comment)
                .build();

        likeRepository.save(createLike);

        return CommentLikeResponse.builder().message("Comment liked").build();

    }

    @Transactional
    @Override
    public void deleteComment(String commentId, String userId) {
        UUID commentUUID = UUID.fromString(commentId);
        Comment comment = commentRepository.findById(commentUUID)
                .orElseThrow(() -> new ResourceNotFoundException("Comment does not exist!"));

        UUID currUserId = UUID.fromString(userId);
        UUID commenterId = comment.getUser().getId();
        if (!commenterId.equals(currUserId)) {
            throw new BadRequestException("Cannot delete other user's comment");
        }

        // 1. Delete all likes from replies
        for(Comment reply : comment.getReplies()) {
            deleteLikesFromReplies(reply);
        }

        // 2. Delete all likes on the comment
        likeRepository.deleteAllByCommentId(comment.getId());
        comment.getLikes().clear();

        // 3. Delete all replies recursively
        for(Comment reply : comment.getReplies()) {
            deleteAllRepliesRecursively(reply);
        }
        comment.getReplies().clear();

        commentRepository.delete(comment);
    }

    private void deleteLikesFromReplies(Comment reply) {
        for(Comment r : new ArrayList<>(reply.getReplies())) {
            deleteLikesFromReplies(r);
        }
        likeRepository.deleteAllByCommentId(reply.getId());
        reply.getLikes().clear();
    }

    private void deleteAllRepliesRecursively(Comment reply) {
        for(Comment r : new ArrayList<>(reply.getReplies())) {
            deleteAllRepliesRecursively(r);
        }

        reply.setPost(null);
        reply.setParentComment(null);
        reply.getReplies().clear();
        commentRepository.delete(reply);
    }

}
