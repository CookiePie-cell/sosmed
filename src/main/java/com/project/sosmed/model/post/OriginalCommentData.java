package com.project.sosmed.model.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OriginalCommentData {
    private UUID Id;
    private UUID commenterId;
    private String commenterUsername;

}
