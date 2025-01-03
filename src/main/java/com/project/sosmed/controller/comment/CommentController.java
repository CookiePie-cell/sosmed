package com.project.sosmed.controller.comment;

import com.project.sosmed.model.PagingResponse;
import com.project.sosmed.model.WebResponse;
import com.project.sosmed.model.comment.CreateCommentRequest;
import com.project.sosmed.model.comment.CreateCommentResponse;
import com.project.sosmed.model.post.CommentRepliesResponse;
import com.project.sosmed.model.post.CreatePostRequest;
import com.project.sosmed.model.post.CreatePostResponse;
import com.project.sosmed.model.post.PostCommentsResponse;
import com.project.sosmed.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<WebResponse<CreateCommentResponse>> createComment(
            Authentication authentication,
            @RequestBody CreateCommentRequest request
    ) {
        request.setUserId(authentication.getName());
        return ResponseEntity.ok(WebResponse.<CreateCommentResponse>builder()
                .data(commentService.createComment(request))
                .build());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<WebResponse<List<PostCommentsResponse>>> getCommentsFromPost(
            @PathVariable("postId") String postId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        Page<PostCommentsResponse> comments = commentService.getCommentsFromPost(postId, page, size);

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

    @GetMapping("/replies/{parentCommentId}")
    public ResponseEntity<WebResponse<List<CommentRepliesResponse>>> getCommentReplies(
            @PathVariable("parentCommentId") String id
    ) {
        List<CommentRepliesResponse> replies = commentService.getCommentReplies(id);

        return ResponseEntity.ok(WebResponse.<List<CommentRepliesResponse>>builder()
                .data(replies)
                .status(HttpStatus.OK.value())
                .message("Sucess")
                .build());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<WebResponse<?>> deleteComment(
            Authentication authentication,
            @PathVariable String commentId
    ) {
        commentService.deleteComment(commentId, authentication.getName());
        return ResponseEntity.ok(WebResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .build());
    }
}
