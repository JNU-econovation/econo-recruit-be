package com.econovation.recruitdomain.domains.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponseDto {
    private Integer id;
    private String content;
    private Long parentId;
    private Integer applicantId;
}
