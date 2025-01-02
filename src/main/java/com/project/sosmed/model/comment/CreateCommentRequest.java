package com.project.sosmed.model.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCommentRequest {
    private String content;
    private String userId;
    private String postId;
    private String parentCommentId;
}
