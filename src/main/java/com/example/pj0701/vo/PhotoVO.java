package com.example.pj0701.vo;

import lombok.Data;

@Data
public class PhotoVO {
    private int photoNo;
    private int shopNo;
    private int articleNo;
    private String originName;
    private String uploadName;
    private String url;
    private String fileSize;
    private String insDate;
    private String rpstYn;
}
