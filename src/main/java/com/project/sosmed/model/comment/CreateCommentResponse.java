package com.project.sosmed.model.comment;

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
@Builder
public class CreateCommentResponse {
    private UUID id;
    private String content;
    private LocalDateTime createdDate;
    private UUID postId;
    private UUID parentCommentId;
    private UUID commenterId;
    private String commenter;
}
