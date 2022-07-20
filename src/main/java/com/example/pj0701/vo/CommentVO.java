package com.example.pj0701.vo;

import lombok.Data;

@Data
public class CommentVO {
    private int commentNo;
    private int grp;
    private int seq;
    private int lv;
    private int articleNo;
    private int parentNo;

    private int userNo;
    private String contents;
    private String insDate;
    private String upDate;
}
