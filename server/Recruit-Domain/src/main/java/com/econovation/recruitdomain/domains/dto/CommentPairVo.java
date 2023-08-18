package com.econovation.recruitdomain.domains.dto;

import com.econovation.recruitdomain.domains.comment.Comment;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentPairVo {
    private LocalDateTime createdAt;
    private String interviewerName;
    private String content;
    private Boolean isLike;
    private Integer likeCount;
    public static CommentPairVo of(Comment comment, Boolean isLike, String interviewerName) {
        return CommentPairVo.builder()
            .createdAt(comment.getCreatedAt())
            .content(comment.getContent())
            .isLike(isLike)
            .likeCount(comment.getLikeCount())
            .interviewerName(interviewerName)
            .build();
    }
}
