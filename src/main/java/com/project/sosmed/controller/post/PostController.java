package com.project.sosmed.controller.post;

import com.project.sosmed.model.PagingResponse;
import com.project.sosmed.model.WebResponse;
import com.project.sosmed.model.post.*;
import com.project.sosmed.service.PostService;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private PostService postService;

    @PostMapping
    public ResponseEntity<WebResponse<CreatePostResponse>> createPost(
            Authentication authentication,
            @RequestBody CreatePostRequest request
    ) {

        request.setUserId(authentication.getName());

        return ResponseEntity.ok(WebResponse.<CreatePostResponse>builder()
                        .data(postService.createPost(request))
                        .status(HttpStatus.OK.value())
                        .message("Success")
                .build());

    }

    @PutMapping("/{postId}")
    public ResponseEntity<WebResponse<UpdatePostResponse>> updatePost(
            @PathVariable String postId,
            @RequestBody UpdatePostRequest request
    ) {
        request.setPostId(postId);

        return ResponseEntity.ok(WebResponse.<UpdatePostResponse>builder()
                        .data(postService.updatePost(request))
                        .status(HttpStatus.OK.value())
                        .message("Success")
                .build());
    }

    @PostMapping("/like/{postId}")
    public ResponseEntity<WebResponse<PostLikeResponse>> likePost(
            Authentication authentication,
            @PathVariable String postId
    ) {
        PostLikeRequest postLikeRequest = PostLikeRequest
                .builder()
                .postId(postId)
                .userId(authentication.getName())
                .build();


        return ResponseEntity.ok(WebResponse.<PostLikeResponse>
                builder()
                        .data(postService.LikePost(postLikeRequest))
                        .status(HttpStatus.OK.value())
                        .message("Success")
                .build());
    }

    @GetMapping("/my-posts")
    public ResponseEntity<WebResponse<List<PostResponse>>> getMyPostsWithTotalLikesAndComments(
            Authentication authentication,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        Page<PostResponse> posts = postService.getMyPostsWithTotalLikesAndComments(authentication.getName(), page, size);
        return ResponseEntity.ok(WebResponse.<List<PostResponse>>builder()
                        .data(posts.getContent())
                        .status(HttpStatus.OK.value())
                        .paging(PagingResponse.builder()
                                .currentPage(posts.getNumber())
                                .totalPage(posts.getTotalPages())
                                .size(posts.getSize())
                                .build())
                        .message("Success")
                .build());
    }

    @GetMapping("/all-posts")
    public ResponseEntity<WebResponse<List<PostResponse>>> getAllPostsWithTotalLikesAndComments(
            Authentication authentication,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        Page<PostResponse> posts = postService.getAllPostsWithTotalLikesAndComments(authentication.getName(), page, size);
        return ResponseEntity.ok(WebResponse.<List<PostResponse>>builder()
                        .data(posts.getContent())
                        .status(HttpStatus.OK.value())
                        .paging(PagingResponse.builder()
                                .currentPage(posts.getNumber())
                                .totalPage(posts.getTotalPages())
                                .size(posts.getSize())
                                .build())
                        .message("Success")
                .build());
    }

    @GetMapping("/followed-user-posts")
    public ResponseEntity<WebResponse<List<PostResponse>>> getFollowedUserPostsWithTotalLikesAndComments(
            Authentication authentication,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        Page<PostResponse> posts = postService.getFollowedUserPostsWithTotalLikesAndComments(
                authentication.getName(),
                page,
                size
        );
        return ResponseEntity.ok(WebResponse.<List<PostResponse>>builder()
                        .data(posts.getContent())
                        .status(HttpStatus.OK.value())
                        .paging(PagingResponse.builder()
                                .currentPage(posts.getNumber())
                                .totalPage(posts.getTotalPages())
                                .size(posts.getSize())
                                .build())
                        .message("Success")
                .build());
    }

    @GetMapping("/all-liked-posts")
    public ResponseEntity<WebResponse<List<PostResponse>>> getAllLikedPosts(
            Authentication authentication,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        Page<PostResponse> posts = postService.getLikedPostsByUser(
                authentication.getName(),
                page,
                size
        );

        return ResponseEntity.ok(WebResponse.<List<PostResponse>>builder()
                .data(posts.getContent())
                .status(HttpStatus.OK.value())
                .paging(PagingResponse.builder()
                        .currentPage(posts.getNumber())
                        .totalPage(posts.getTotalPages())
                        .size(posts.getSize())
                        .build())
                .message("Success")
                .build());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<WebResponse<?>> deletePost(
            Authentication authentication,
            @PathVariable String postId
    ) {
        String userId = authentication.getName();
        postService.deletePost(postId, userId);
        return ResponseEntity.ok(WebResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .build());
    }
}
