package com.example.pj0701.vo;

import lombok.Data;


@Data
public class ArticleVO {
    private int shopNo;

    private int articleNo;
    private int userNo;
    private String title;
    private String contents;

    private int hitCnt;
    private int likeCnt;
    private String insDate;
    private String upDate;

    private int photoNo;
    private String url;
}
