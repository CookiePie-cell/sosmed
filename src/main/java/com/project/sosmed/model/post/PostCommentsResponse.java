package com.project.sosmed.model.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentsResponse {
    private UUID commentId;
    private String content;
    private Date createdDate;
    private Date deletedDate;
    private UUID parentCommentId;
    private UUID postId;
    private UUID commenterId;
    private String commenter;
    private Long likeCount;
    private Long replyCount;

}