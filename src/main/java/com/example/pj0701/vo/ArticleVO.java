package com.example.pj0701.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "게시글VO")
@Data
public class ArticleVO {
    @Schema(description = "가게 번호")
    private int shopNo;

    @Schema(description = "글 번호")
    private int articleNo;
    @Schema(description = "글쓴이 번호")
    private int userNo;
    @Schema(description = "글 제목")
    private String title;
    @Schema(description = "글 내용")
    private String contents;

    @Schema(description = "글 조회수")
    private int hitCnt;
    @Schema(description = "글 좋아요수")
    private int likeCnt;
    @Schema(description = "글 작성일자")
    private String insDate;
    @Schema(description = "글 수정일자")
    private String upDate;

    @Schema(description = "글 대표사진 번호")
    private int photoNo;
    @Schema(description = "글 대표사진 경로")
    private String url;

    @Schema(description = "글 좋아요 여부")
    private String likeYn;
}
