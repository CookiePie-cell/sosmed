package com.project.sosmed.controller.post;

import com.project.sosmed.model.PagingResponse;
import com.project.sosmed.model.WebResponse;
import com.project.sosmed.model.post.*;
import com.project.sosmed.service.PostService;
import lombok.AllArgsConstructor;
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
                        .message("Success")
                .build());
    }

    @GetMapping("/comments/{postId}")
    public ResponseEntity<WebResponse<List<PostCommentsResponse>>> getCommentsFromPost(
            @PathVariable("postId") String postId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        Page<PostCommentsResponse> comments = postService.getCommentsFromPost(postId, page, size);

        return ResponseEntity.ok(WebResponse.<List<PostCommentsResponse>>builder()
                        .data(comments.getContent())
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .paging(PagingResponse.builder()
                                .currentPage(comments.getNumber())
                                .totalPage(comments.getTotalPages())
                                .size(comments.getSize())
                                .build())
                .build());
    }

    @GetMapping("/comments/replies/{parentCommentId}")
    public ResponseEntity<WebResponse<List<CommentRepliesResponse>>> getCommentReplies(
            @PathVariable("parentCommentId") String id
    ) {
        List<CommentRepliesResponse> replies = postService.getCommentReplies(id);

        return ResponseEntity.ok(WebResponse.<List<CommentRepliesResponse>>builder()
                        .data(replies)
                        .status(HttpStatus.OK.value())
                        .message("Sucess")
                .build());
    }
}
