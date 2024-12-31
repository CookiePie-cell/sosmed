package com.project.sosmed.model.post;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PostResponse {
    private UUID postId;
    private String postBody;
    private UUID userId;
    private String postCreatorUsername;
    private Date createdDate;
    private Date updatedDate;
    private Long totalComments;
    private Long totalLikes;

    public PostResponse(UUID postId, String postBody, UUID userId, String postCreatorUsername,
                        Date createdDate, Date updatedDate, Long totalComments, Long totalLikes) {
        this.postId = postId;
        this.postBody = postBody;
        this.userId = userId;
        this.postCreatorUsername = postCreatorUsername;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.totalComments = totalComments;
        this.totalLikes = totalLikes;
    }

    private boolean isLiked;
}


