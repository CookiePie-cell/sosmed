package com.project.sosmed.model.post;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PostResponse {
    private UUID postId;
    private String postBody;
    private UUID userId;
    private String postCreatorUsername;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long totalComments;
    private Long totalLikes;

    public PostResponse(UUID postId, String postBody, UUID userId, String postCreatorUsername,
                        LocalDateTime createdDate, LocalDateTime updatedDate, Long totalComments, Long totalLikes) {
        this.postId = postId;
        this.postBody = postBody;
        this.userId = userId;
        this.postCreatorUsername = postCreatorUsername;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.totalComments = totalComments;
        this.totalLikes = totalLikes;
    }

    public PostResponse(boolean isLiked) {
        this.isLiked = isLiked;
    }

    private boolean isLiked;
}


