package com.project.sosmed.service;

import com.project.sosmed.model.post.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {

    CreatePostResponse createPost(CreatePostRequest request);

    UpdatePostResponse updatePost(UpdatePostRequest request);

    PostLikeResponse LikePost(PostLikeRequest request);

    Page<PostResponse> getAllPostsWithTotalLikesAndComments(String userId, int page, int size);

    Page<PostResponse> getFollowedUserPostsWithTotalLikesAndComments(String userId, int page, int size);

    void deletePost(String postId, String userId);

    Page<PostResponse> getLikedPostsByUser(String userId, int page, int size);
}
