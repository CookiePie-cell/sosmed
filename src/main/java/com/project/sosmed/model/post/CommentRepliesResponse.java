package com.project.sosmed.model.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRepliesResponse {
    private UUID id;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime deletedDate;
    private OriginalCommentData originalComment;
    private UUID postId;
    private UUID replierId;
    private String replier;
    private Long likeCount;
}
