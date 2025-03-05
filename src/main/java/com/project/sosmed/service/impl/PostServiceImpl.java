package com.project.sosmed.service.impl;

import com.project.sosmed.entity.*;
import com.project.sosmed.exception.BadRequestException;
import com.project.sosmed.exception.ResourceNotFoundException;
import com.project.sosmed.model.post.*;
import com.project.sosmed.repository.CommentRepository;
import com.project.sosmed.repository.LikeRepository;
import com.project.sosmed.repository.PostRepository;
import com.project.sosmed.repository.UserRepository;
import com.project.sosmed.service.FileService;
import com.project.sosmed.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    private CommentRepository commentRepository;

    private UserRepository userRepository;

    private LikeRepository likeRepository;

    private FileService fileService;

    @Transactional
    @Override
    public CreatePostResponse createPost(CreatePostRequest request, List<MultipartFile> media) {
        UUID userId = UUID.fromString(request.getUserId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = Post.builder()
                .body(request.getContent())
                .user(user)
                .build();

        Post createdPost = postRepository.save(post);

        if (media != null && !media.isEmpty()) {
            List<FileMetadata> fileToSave = media.stream().map(m -> {
                FileMetadata file = fileService.uploadFile(m);
                file.setPost(post);
                return file;
            }).toList();

            fileService.saveAll(fileToSave);
        }

        return CreatePostResponse.builder()
                .postId(createdPost.getId())
                .build();
    }

    @Override
    public UpdatePostResponse updatePost(UpdatePostRequest request) {
        UUID postId = UUID.fromString(request.getPostId());

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        post.setBody(request.getContent());

        postRepository.save(post);

        return UpdatePostResponse.builder()
                .postId(postId)
                .build();
    }

    @Override
    public PostLikeResponse LikePost(PostLikeRequest request) {
        UUID userId = UUID.fromString(request.getUserId());
        UUID postId = UUID.fromString(request.getPostId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Optional<Like> like = likeRepository.findByUserAndPost(user, post);

        if (like.isPresent()) {
            likeRepository.delete(like.get());
            return PostLikeResponse.builder().message("Post unliked").build();
        }

        Like createLike = Like.builder()
                .user(user)
                .post(post)
                .build();

        likeRepository.save(createLike);

        return PostLikeResponse.builder().message("Post liked").build();

    }

    @Override
    public Page<PostResponse> getMyPostsWithTotalLikesAndComments(String userId, int page, int size) {
        UUID userIdUUID = UUID.fromString(userId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<PostResponse> posts = postRepository.getMyPostsWithTotalLikesAndComments(userIdUUID, pageable);

        return getPostResponses(userIdUUID, posts);
    }

    @Override
    public Page<PostResponse> getAllPostsWithTotalLikesAndComments(String userId, int page, int size) {
        UUID userIdUUID = UUID.fromString(userId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<PostResponse> posts = postRepository.getAllPostsWithTotalLikesAndComments(pageable);

        return getPostResponses(userIdUUID, posts);
    }

    @Override
    public Page<PostResponse> getFollowedUserPostsWithTotalLikesAndComments(String userId, int page, int size) {
        UUID userIdUUID = UUID.fromString(userId);

        if (!userRepository.existsById(userIdUUID)) {
            throw new ResourceNotFoundException("User not found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<PostResponse> posts =  postRepository.getFollowedUserPostsWithTotalLikesAndComments(userIdUUID, pageable);

        return getPostResponses(userIdUUID, posts);
    }

    @Transactional
    @Override
    public void deletePost(String postId, String userId) {
        UUID postUUID = UUID.fromString(postId);
        UUID currUserUUID = UUID.fromString(userId);

        Post post = postRepository.findByPostIdAndComments(postUUID)
                .orElseThrow(() -> new ResourceNotFoundException("Post does not exist!"));

        UUID postCreatorUUID = post.getUser().getId();
        if (!currUserUUID.equals(postCreatorUUID)) {
            throw new BadRequestException("Cannot delete other user's posts");
        }

        // 1. Delete all likes from all nested comments
        for (Comment comment : post.getComments()) {
            deleteLikesFromComment(comment);
        }

        // 2. Delete all likes directly on the post
        likeRepository.deleteAllByPostId(postUUID);
        post.getLikes().clear();

        // 3. Delete all comments recursively
        for (Comment comment : new ArrayList<>(post.getComments())) {
            deleteCommentsRecursively(comment);
        }
        post.getComments().clear();

        // 4. Finally delete the post
        postRepository.delete(post);
    }

    @Override
    public Page<PostResponse> getLikedPostsByUser(String userId, int page, int size){
        UUID userIdUUID = UUID.fromString(userId);

        if (!userRepository.existsById(userIdUUID)) {
            throw new ResourceNotFoundException("User not found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<PostResponse> posts = postRepository.getLikedPostsByUser(userIdUUID, pageable);

        return getPostResponses(userIdUUID, posts);
    }

    private void deleteLikesFromComment(Comment comment) {
        for (Comment reply : new ArrayList<>(comment.getReplies())) {
            deleteLikesFromComment(reply);
        }

        likeRepository.deleteAllByCommentId(comment.getId());
        comment.getLikes().clear();
    }

    private void deleteCommentsRecursively(Comment comment) {
        for (Comment reply : new ArrayList<>(comment.getReplies())) {
            deleteCommentsRecursively(reply);
        }

        comment.setPost(null);
        comment.setParentComment(null);
        comment.getReplies().clear();

        commentRepository.delete(comment);
    }

    private Page<PostResponse> getPostResponses(UUID userId, Page<PostResponse> posts) {
        List<UUID> postIds = posts.getContent().stream().map(
                PostResponse::getPostId
        ).toList();

        List<UUID> likedPostIds = likeRepository.findLikedPostIdByUserIdAndPostId(userId, postIds);

        posts.getContent().forEach(post -> post.setLiked(likedPostIds.contains(post.getPostId())));

        return posts;
    }

}
